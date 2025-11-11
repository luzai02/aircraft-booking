package cn.luzai.aircraftserver.server;

import cn.luzai.aircraftpojo.dto.reequest.BookingCancelRequest;
import cn.luzai.aircraftpojo.dto.response.BookingCancelResponse;
import cn.luzai.aircraftpojo.entity.core.Booking;
import cn.luzai.aircraftpojo.entity.core.BookingSegment;
import cn.luzai.aircraftpojo.entity.enums.BookingStatus;
import cn.luzai.aircraftpojo.entity.log.BookingLog;
import cn.luzai.aircraftserver.ddc.DataSourceContextHolder;
import cn.luzai.aircraftserver.exception.BusinessException;
import cn.luzai.aircraftserver.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单取消服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookingCancelService {

    private final BookingMapper bookingMapper;
    private final BookingSegmentMapper bookingSegmentMapper;
    private final SeatMapper seatMapper;
    private final FlightInstanceMapper flightInstanceMapper;
    private final BookingLogMapper bookingLogMapper;

    /**
     * 取消订单
     */
    public BookingCancelResponse cancelBooking(BookingCancelRequest request) {
        log.info("开始取消订单，bookingId={}, reason={}",
                request.getBookingId(), request.getCancelReason());

        Booking booking = null;
        List<BookingSegment> segments = null;
        String targetDataSource = null;

        try {
            // ============================================================
            // Step 1: 遍历业务库查询订单和航段
            // ============================================================
            for (String dataSource : List.of("airline-a", "airline-b", "airline-c")) {
                try {
                    DataSourceContextHolder.setDataSource(dataSource);
                    log.debug("尝试查询数据源: {}", dataSource);

                    // 查询订单
                    booking = bookingMapper.findByBookingId(request.getBookingId());

                    if (booking != null) {
                        log.info("在数据源 {} 中找到订单", dataSource);

                        // 查询航段
                        segments = bookingSegmentMapper.findByBookingId(request.getBookingId());

                        targetDataSource = dataSource;
                        break;  // 找到订单后跳出循环（不清除数据源）
                    }

                } catch (Exception e) {
                    log.error("查询数据源 {} 失败", dataSource, e);
                } finally {
                    // 只有在没找到订单时才清除数据源
                    if (booking == null) {
                        DataSourceContextHolder.clearDataSource();
                    }
                }
            }

            // ============================================================
            // Step 2: 校验订单是否存在
            // ============================================================
            if (booking == null) {
                throw new BusinessException("订单不存在");
            }

            if (segments == null || segments.isEmpty()) {
                throw new BusinessException("订单航段信息不存在");
            }

            // ============================================================
            // Step 3: 校验订单状态
            // ============================================================
            if (!BookingStatus.HOLD.equals(booking.getStatus()) &&
                    !BookingStatus.CONFIRMED.equals(booking.getStatus())) {
                throw new BusinessException("订单状态不允许取消，当前状态: " + booking.getStatus().getDescription());
            }

            BookingSegment segment = segments.get(0);

            // ============================================================
            // Step 4: 校验是否已起飞
            // ============================================================
            if (segment.getDepartDatetime().isBefore(LocalDateTime.now())) {
                throw new BusinessException("航班已起飞，无法取消订单");
            }

            // ============================================================
            // Step 5: 取消订单（数据源已经是正确的业务库）
            // ============================================================
            log.info("当前数据源: {}", targetDataSource);

            LocalDateTime cancelTime = LocalDateTime.now();
            int updated = bookingMapper.cancelBooking(
                    request.getBookingId(),
                    request.getCancelReason() != null ? request.getCancelReason() : "用户主动取消",
                    cancelTime
            );

            if (updated == 0) {
                throw new BusinessException("订单取消失败，可能已被取消");
            }

            log.info("订单状态更新成功，bookingId={}", request.getBookingId());

            // ============================================================
            // Step 6: 释放座位（SOLD → AVAILABLE）
            // ============================================================
            int releasedSeats = seatMapper.releaseSoldSeats(request.getBookingId());
            log.info("释放座位成功，数量: {}", releasedSeats);

            // ============================================================
            // Step 7: 恢复元数据库的余座
            // ============================================================
            DataSourceContextHolder.setDataSource("meta");
            log.debug("切换到数据源: meta，准备恢复余座");

            int restored = flightInstanceMapper.increaseAvailableSeats(
                    segment.getInstanceId(),
                    segment.getSeatCount()
            );

            if (restored > 0) {
                log.info("恢复余座成功，instanceId={}, count={}",
                        segment.getInstanceId(), segment.getSeatCount());
            } else {
                log.warn("恢复余座失败，instanceId={}", segment.getInstanceId());
            }

            // ============================================================
            // Step 8: 记录操作日志
            // ============================================================
            DataSourceContextHolder.setDataSource(targetDataSource);
            logBookingOperation(
                    request.getBookingId(),
                    "CANCEL",
                    booking.getStatus().getCode(),
                    BookingStatus.CANCELLED.getCode(),
                    request.getCancelReason()
            );

            log.info("订单取消成功，bookingId={}", request.getBookingId());

            return BookingCancelResponse.builder()
                    .bookingId(request.getBookingId())
                    .status("SUCCESS")
                    .cancelTime(cancelTime)
                    .message("订单取消成功")
                    .build();

        } catch (Exception e) {
            log.error("取消订单失败，bookingId={}, error={}", request.getBookingId(), e.getMessage(), e);

            return BookingCancelResponse.builder()
                    .bookingId(request.getBookingId())
                    .status("FAILED")
                    .message(e.getMessage())
                    .build();

        } finally {
            DataSourceContextHolder.clearDataSource();
        }
    }

    /**
     * 记录订单操作日志
     */
    private void logBookingOperation(String bookingId, String operation,
                                     String oldStatus, String newStatus, String remark) {
        BookingLog log = BookingLog.builder()
                .bookingId(bookingId)
                .operation(operation)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .operator("USER")
                .remark(remark)
                .build();

        bookingLogMapper.insert(log);
    }
}