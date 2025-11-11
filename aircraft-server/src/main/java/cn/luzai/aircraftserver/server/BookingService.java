package cn.luzai.aircraftserver.server;

import cn.luzai.aircraftpojo.dto.reequest.BookingRequest;
import cn.luzai.aircraftpojo.dto.reequest.PassengerInfo;
import cn.luzai.aircraftpojo.dto.response.BookingResponse;
import cn.luzai.aircraftpojo.dto.response.SeatMapResponse;
import cn.luzai.aircraftpojo.entity.core.*;
import cn.luzai.aircraftpojo.entity.enums.BookingStatus;
import cn.luzai.aircraftpojo.entity.enums.BookingType;
import cn.luzai.aircraftpojo.entity.log.BookingLog;
import cn.luzai.aircraftpojo.entity.meta.FlightInstance;
import cn.luzai.aircraftserver.ddc.DataSourceContextHolder;
import cn.luzai.aircraftserver.exception.BusinessException;
import cn.luzai.aircraftserver.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final FlightInstanceMapper flightInstanceMapper;
    private final SeatMapper seatMapper;
    private final PassengerMapper passengerMapper;
    private final BookingMapper bookingMapper;
    private final BookingSegmentMapper bookingSegmentMapper;
    private final BookingPassengerSeatMapper bookingPassengerSeatMapper;
    private final BookingLogMapper bookingLogMapper;

    private static final int HOLD_EXPIRE_MINUTES = 15;  // 订单保留时间（15分钟）

    /**
     * 查询座位图（使用元数据库查询航班信息，然后切换到业务库查询座位）
     */
    public SeatMapResponse getSeatMap(String instanceId) {
        // Step 1: 从元数据库查询航班实例
        DataSourceContextHolder.setDataSource("meta");
        FlightInstance flightInstance = flightInstanceMapper.findByInstanceId(instanceId);

        if (flightInstance == null) {
            throw new BusinessException("航班不存在");
        }

        // Step 2: 根据航司代码切换到对应业务库
        String dataSource = getDataSourceByAirlineCode(flightInstance.getAirlineCode());
        DataSourceContextHolder.setDataSource(dataSource);

        // Step 3: 查询座位图
        List<SeatMapResponse.SeatInfo> seats = seatMapper.findSeatMapByInstanceId(instanceId);

        if (seats.isEmpty()) {
            throw new BusinessException("座位数据未初始化，请联系管理员");
        }

        // 清除数据源上下文
        DataSourceContextHolder.clearDataSource();

        return SeatMapResponse.builder()
                .instanceId(instanceId)
                .totalRows(seats.stream().mapToInt(SeatMapResponse.SeatInfo::getRowNumber).max().orElse(0))
                .seatsPerRow(6)  // 标准 3-3 布局
                .seatLayout("ABC-DEF")
                .seats(seats)
                .build();
    }

    /**
     * 创建订单（核心方法）
     */
    @Transactional(rollbackFor = Exception.class)
    public BookingResponse createBooking(BookingRequest request) {
        log.info("开始创建订单，instanceId={}, passengers={}, seats={}",
                request.getInstanceId(),
                request.getPassengers().size(),
                request.getSeatNumbers());

        // Step 1: 幂等性检查
        Booking existingBooking = bookingMapper.findByClientToken(request.getClientToken());
        if (existingBooking != null) {
            log.warn("订单已存在，clientToken={}", request.getClientToken());
            return buildBookingResponse(existingBooking);
        }

        // Step 2: 查询航班信息（从元数据库）
        DataSourceContextHolder.setDataSource("meta");
        FlightInstance flightInstance = flightInstanceMapper.findByInstanceId(request.getInstanceId());

        if (flightInstance == null) {
            throw new BusinessException("航班不存在");
        }

        if (flightInstance.getAvailableSeats() < request.getPassengers().size()) {
            throw new BusinessException("航班余座不足");
        }

        // Step 3: 切换到对应的业务库
        String dataSource = getDataSourceByAirlineCode(flightInstance.getAirlineCode());
        DataSourceContextHolder.setDataSource(dataSource);

        // Step 4: 校验并锁定座位
        List<Seat> selectedSeats = lockSeats(request.getInstanceId(), request.getSeatNumbers());

        // Step 5: 保存乘客信息
        List<Passenger> passengers = savePassengers(request.getPassengers());

        // Step 6: 生成订单号
        String bookingId = generateBookingId();

        // Step 7: 计算价格
        BigDecimal totalPrice = flightInstance.getPrice().multiply(new BigDecimal(passengers.size()));

        // Step 8: 创建订单
        Booking booking = Booking.builder()
                .bookingId(bookingId)
                .bookingType(BookingType.SINGLE)
                .passengerCount(passengers.size())
                .contactPassengerId(passengers.get(request.getContactPassengerIndex()).getPassengerId())
                .totalOriginalPrice(totalPrice)
                .discountAmount(BigDecimal.ZERO)
                .totalPrice(totalPrice)
                .currency("CNY")
                .status(BookingStatus.HOLD)
                .holdExpireAt(LocalDateTime.now().plusMinutes(HOLD_EXPIRE_MINUTES))
                .bookingTime(LocalDateTime.now())
                .clientToken(request.getClientToken())
                .remark(request.getRemark())
                .build();

        bookingMapper.insert(booking);

        // Step 9: 创建航段记录
        BookingSegment segment = BookingSegment.builder()
                .bookingId(bookingId)
                .sequence(1)
                .instanceId(request.getInstanceId())
                .flightNumber(flightInstance.getFlightNumber())
                .airlineCode(flightInstance.getAirlineCode())
                .departAirport(flightInstance.getDepartAirport())
                .arrivalAirport(flightInstance.getArrivalAirport())
                .departDatetime(flightInstance.getDepartDatetime())
                .arrivalDatetime(flightInstance.getArrivalDatetime())
                .seatCount(passengers.size())
                .segmentPrice(flightInstance.getPrice())
                .build();

        bookingSegmentMapper.insert(segment);

        // Step 10: 关联乘客与座位
        List<BookingPassengerSeat> passengerSeats = new ArrayList<>();
        for (int i = 0; i < passengers.size(); i++) {
            Passenger passenger = passengers.get(i);
            Seat seat = selectedSeats.get(i);

            passengerSeats.add(BookingPassengerSeat.builder()
                    .bookingId(bookingId)
                    .segmentId(segment.getSegmentId())
                    .passengerId(passenger.getPassengerId())
                    .seatId(seat.getSeatId())
                    .seatNumber(seat.getSeatNumber())
                    .seatPreference(request.getPassengers().get(i).getSeatPreference())
                    .build());
        }

        bookingPassengerSeatMapper.batchInsert(passengerSeats);

        // Step 11: 扣减元数据库的余座（切换回元数据库）
        DataSourceContextHolder.setDataSource("meta");
        int updated = flightInstanceMapper.decreaseAvailableSeats(request.getInstanceId(), passengers.size());
        if (updated == 0) {
            throw new BusinessException("扣减余座失败，可能余座不足");
        }

        // Step 12: 记录操作日志
        DataSourceContextHolder.setDataSource(dataSource);
        logBookingOperation(bookingId, "CREATE", null, BookingStatus.HOLD.getCode());

        // 清除数据源上下文
        DataSourceContextHolder.clearDataSource();

        log.info("订单创建成功，bookingId={}", bookingId);

        return buildBookingResponse(booking);
    }

    /**
     * 锁定座位
     */
    private List<Seat> lockSeats(String instanceId, List<String> seatNumbers) {
        // 查询座位
        List<Seat> seats = seatMapper.findByInstanceIdAndSeatNumbers(instanceId, seatNumbers);

        // 校验座位数量
        if (seats.size() != seatNumbers.size()) {
            throw new BusinessException("部分座位不存在");
        }

        // 校验座位状态
        List<String> unavailableSeats = seats.stream()
                .filter(seat -> !"AVAILABLE".equals(seat.getStatus().getCode()))
                .map(Seat::getSeatNumber)
                .collect(Collectors.toList());

        if (!unavailableSeats.isEmpty()) {
            throw new BusinessException("座位 " + String.join(", ", unavailableSeats) + " 已被占用");
        }

        // 锁定座位（15分钟）
        List<Long> seatIds = seats.stream().map(Seat::getSeatId).collect(Collectors.toList());
        String tempBookingId = "TEMP_" + System.currentTimeMillis();
        LocalDateTime lockExpireAt = LocalDateTime.now().plusMinutes(HOLD_EXPIRE_MINUTES);

        int locked = seatMapper.lockSeats(seatIds, tempBookingId, lockExpireAt);

        if (locked != seatIds.size()) {
            throw new BusinessException("座位锁定失败，请重新选择");
        }

        return seats;
    }

    /**
     * 保存乘客信息
     */
    private List<Passenger> savePassengers(List<PassengerInfo> passengerInfos) {
        List<Passenger> passengers = new ArrayList<>();

        for (PassengerInfo info : passengerInfos) {
            // 检查是否已存在（根据证件号）
            Passenger existing = passengerMapper.findByIdNumber(info.getIdNumber());

            if (existing != null) {
                passengers.add(existing);
            } else {
                Passenger passenger = Passenger.builder()
                        .passengerName(info.getPassengerName())
                        .idType(info.getIdType())
                        .idNumber(info.getIdNumber())
                        .phone(info.getPhone())
                        .email(info.getEmail())
                        .passengerType(info.getPassengerType())
                        .build();

                passengerMapper.insert(passenger);
                passengers.add(passenger);
            }
        }

        return passengers;
    }

    /**
     * 生成订单号
     */
    private String generateBookingId() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return "BK" + LocalDateTime.now().format(formatter) + (int)(Math.random() * 1000);
    }

    /**
     * 记录订单操作日志
     */
    private void logBookingOperation(String bookingId, String operation, String oldStatus, String newStatus) {
        BookingLog log = BookingLog.builder()
                .bookingId(bookingId)
                .operation(operation)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .operator("SYSTEM")
                .build();

        bookingLogMapper.insert(log);
    }

    /**
     * 构建订单响应
     */
    private BookingResponse buildBookingResponse(Booking booking) {
        // TODO: 查询完整的订单详情（航班信息、乘客座位分配）
        return BookingResponse.builder()
                .bookingId(booking.getBookingId())
                .status(booking.getStatus())
                .totalOriginalPrice(booking.getTotalOriginalPrice())
                .discountAmount(booking.getDiscountAmount())
                .totalPrice(booking.getTotalPrice())
                .bookingTime(booking.getBookingTime())
                .holdExpireAt(booking.getHoldExpireAt())
                .build();
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
