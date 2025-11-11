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

        String dataSource = null;

        try {
            // Step 1: 查询订单信息（需要遍历3个业务库）
            Booking booking = findBookingFromAllDataSources(request.getBookingId());

            if (booking == null) {
                throw new BusinessException("订单不存在");
            }

            // Step 2: 校验订单状态
            if (!BookingStatus.HOLD.equals(booking.getStatus()) &&
                    !BookingStatus.CONFIRMED.equals(booking.getStatus())) {
                throw new BusinessException("订单状态不允许取消，当前状态: " + booking.getStatus().getDescription());
            }

            // Step 3: 查询航段信息
            List<BookingSegment> segments = bookingSegmentMapper.findByBookingId(request.getBookingId());

            if (segments.isEmpty()) {
                throw new BusinessException("订单航段信息不存在");
            }

            BookingSegment segment = segments.get(0);

            // Step 4: 校验是否已起飞
            if (segment.getDepartDatetime().isBefore(LocalDateTime.now())) {
                throw new BusinessException("航班已起飞，无法取消订单");
            }

            // Step 5: 确定数据源
            dataSource = getDataSourceByAirlineCode(segment.getAirlineCode());
            DataSourceContextHolder.setDataSource(dataSource);
            log.info("切换到业务库: {}", dataSource);

            // Step 6: 取消订单（更新订单状态）
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

            // Step 7: 释放座位（SOLD → AVAILABLE）
            int releasedSeats = seatMapper.releaseSoldSeats(request.getBookingId());
            log.info("释放座位成功，数量: {}", releasedSeats);

            // Step 8: 恢复元数据库的余座
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

            // Step 9: 记录操作日志
            DataSourceContextHolder.setDataSource(dataSource);
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
     * 从所有业务库查询订单
     */
    private Booking findBookingFromAllDataSources(String bookingId) {
        for (String dataSource : List.of("airline-a", "airline-b", "airline-c")) {
            try {
                DataSourceContextHolder.setDataSource(dataSource);
                Booking booking = bookingMapper.findByBookingId(bookingId);

                if (booking != null) {
                    log.debug("在数据源 {} 中找到订单", dataSource);
                    return booking;
                }
            } catch (Exception e) {
                log.error("查询数据源 {} 失败", dataSource, e);
            } finally {
                DataSourceContextHolder.clearDataSource();
            }
        }

        return null;
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

    /**
     * 根据航司代码获取数据源名称
     */
    private String getDataSourceByAirlineCode(String airlineCode) {
        Map<String, String> mapping = Map.of(
                "CA", "airline-a", "ZH", "airline-a", "SC", "airline-a",
                "CZ", "airline-b", "MF", "airline-b", "HU", "airline-b",
                "MU", "airline-c", "FM", "airline-c", "3U", "airline-c"
        );

        String dataSource = mapping.get(airlineCode);
        if (dataSource == null) {
            throw new BusinessException("未知的航司代码: " + airlineCode);
        }
        return dataSource;
    }
}
