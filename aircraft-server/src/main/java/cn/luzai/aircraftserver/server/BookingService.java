package cn.luzai.aircraftserver.server;

import cn.luzai.aircraftpojo.dto.reequest.BookingRequest;
import cn.luzai.aircraftpojo.dto.reequest.PassengerInfo;
import cn.luzai.aircraftpojo.dto.response.BookingResponse;
import cn.luzai.aircraftpojo.dto.response.SeatMapResponse;
import cn.luzai.aircraftpojo.entity.core.*;
import cn.luzai.aircraftpojo.entity.enums.BookingStatus;
import cn.luzai.aircraftpojo.entity.enums.BookingType;
import cn.luzai.aircraftpojo.entity.log.BookingLog;
import cn.luzai.aircraftpojo.entity.meta.Airline;
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
    private final AirlineMapper airlineMapper;

    private static final int HOLD_EXPIRE_MINUTES = 15;

    /**
     * 查询座位图
     */
    public SeatMapResponse getSeatMap(String instanceId) {
        try {
            // Step 1: 从元数据库查询航班实例
            DataSourceContextHolder.setDataSource("meta");
            FlightInstance flightInstance = flightInstanceMapper.findByInstanceId(instanceId);

            if (flightInstance == null) {
                throw new BusinessException("航班不存在");
            }

            // Step 2: 切换到业务库
            String dataSource = getDataSourceByAirlineCode(flightInstance.getAirlineCode());
            DataSourceContextHolder.setDataSource(dataSource);

            // Step 3: 查询座位图
            List<SeatMapResponse.SeatInfo> seats = seatMapper.findSeatMapByInstanceId(instanceId);

            if (seats.isEmpty()) {
                throw new BusinessException("座位数据未初始化，请联系管理员");
            }

            return SeatMapResponse.builder()
                    .instanceId(instanceId)
                    .totalRows(seats.stream().mapToInt(SeatMapResponse.SeatInfo::getRowNumber).max().orElse(0))
                    .seatsPerRow(6)
                    .seatLayout("ABC-DEF")
                    .seats(seats)
                    .build();

        } finally {
            DataSourceContextHolder.clearDataSource();
        }
    }

    /**
     * 创建订单
     * 移除了 @Transactional 注解，改为手动管理数据源切换
     */
    /**
     * 创建订单（核心方法）
     */
    public BookingResponse createBooking(BookingRequest request) {
        log.info("开始创建订单，instanceId={}, passengers={}, seats={}",
                request.getInstanceId(),
                request.getPassengers().size(),
                request.getSeatNumbers());

        FlightInstance flightInstance = null;
        String dataSource = null;
        String bookingId = null;
        Booking booking = null;
        List<Passenger> passengers = null;
        List<Seat> selectedSeats = null;

        try {
            // Step 1: 查询航班信息（从元数据库）
            DataSourceContextHolder.setDataSource("meta");
            log.debug("切换到数据源: meta");

            flightInstance = flightInstanceMapper.findByInstanceId(request.getInstanceId());

            if (flightInstance == null) {
                throw new BusinessException("航班不存在");
            }

            if (flightInstance.getAvailableSeats() < request.getPassengers().size()) {
                throw new BusinessException("航班余座不足");
            }

            // Step 2: 切换到对应的业务库
            dataSource = getDataSourceByAirlineCode(flightInstance.getAirlineCode());
            DataSourceContextHolder.setDataSource(dataSource);
            log.info("切换到业务库: {}", dataSource);

            // Step 3: 幂等性检查
            Booking existingBooking = bookingMapper.findByClientToken(request.getClientToken());
            if (existingBooking != null) {
                log.warn("订单已存在，clientToken={}", request.getClientToken());

                // 查询完整的订单信息（包含航班和乘客座位）
                return buildCompleteBookingResponse(existingBooking, flightInstance);
            }

            // Step 4: 锁定座位
            selectedSeats = lockSeats(request.getInstanceId(), request.getSeatNumbers());

            // Step 5: 保存乘客信息
            passengers = savePassengers(request.getPassengers());

            // Step 6: 生成订单号
            bookingId = generateBookingId();

            // Step 7: 计算价格
            BigDecimal totalPrice = flightInstance.getPrice().multiply(new BigDecimal(passengers.size()));

            // Step 8: 创建订单
            booking = Booking.builder()
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
            log.info("订单创建成功: {}", bookingId);

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
            log.debug("航段记录创建成功: segmentId={}", segment.getSegmentId());

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
            log.debug("乘客座位关联创建成功，数量: {}", passengerSeats.size());

            // Step 11: 扣减元数据库的余座
            DataSourceContextHolder.setDataSource("meta");
            log.debug("切换到数据源: meta，准备扣减余座");

            int updated = flightInstanceMapper.decreaseAvailableSeats(
                    request.getInstanceId(),
                    passengers.size()
            );

            if (updated == 0) {
                log.error("扣减余座失败，instanceId={}, seatCount={}",
                        request.getInstanceId(), passengers.size());
                throw new BusinessException("扣减余座失败，可能余座不足");
            }

            log.info("扣减余座成功，数量: {}", passengers.size());

            // Step 12: 记录操作日志
            DataSourceContextHolder.setDataSource(dataSource);
            logBookingOperation(bookingId, "CREATE", null, BookingStatus.HOLD.getCode());

            log.info("订单创建完成，bookingId={}", bookingId);

            // Step 13: 构建完整的响应（包含航班信息和乘客座位）
            return buildBookingResponseWithData(
                    booking,
                    flightInstance,
                    passengers,
                    selectedSeats
            );

        } catch (Exception e) {
            log.error("创建订单失败，instanceId={}, error={}", request.getInstanceId(), e.getMessage(), e);

            // 补偿操作：释放已锁定的座位
            if (dataSource != null && bookingId != null) {
                try {
                    DataSourceContextHolder.setDataSource(dataSource);
                    seatMapper.releaseSeats(bookingId);
                    log.info("已释放锁定的座位，bookingId={}", bookingId);
                } catch (Exception releaseException) {
                    log.error("释放座位失败", releaseException);
                }
            }

            throw e instanceof BusinessException ? (BusinessException) e : new BusinessException("创建订单失败", e);

        } finally {
            // 确保清除数据源上下文
            DataSourceContextHolder.clearDataSource();
        }
    }

    /**
     * 锁定座位
     */
    private List<Seat> lockSeats(String instanceId, List<String> seatNumbers) {
        log.debug("开始锁定座位，instanceId={}, seatNumbers={}", instanceId, seatNumbers);

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
        // todo 这里可以
        List<Long> seatIds = seats.stream().map(Seat::getSeatId).collect(Collectors.toList());
        String tempBookingId = "TEMP_" + System.currentTimeMillis();
        LocalDateTime lockExpireAt = LocalDateTime.now().plusMinutes(HOLD_EXPIRE_MINUTES);

        int locked = seatMapper.lockSeats(seatIds, tempBookingId, lockExpireAt);

        if (locked != seatIds.size()) {
            throw new BusinessException("座位锁定失败，请重新选择");
        }

        log.info("座位锁定成功，数量: {}", locked);
        return seats;
    }

    /**
     * 保存乘客信息
     */
    private List<Passenger> savePassengers(List<PassengerInfo> passengerInfos) {
        log.debug("开始保存乘客信息，数量: {}", passengerInfos.size());

        List<Passenger> passengers = new ArrayList<>();

        for (PassengerInfo info : passengerInfos) {
            // 检查是否已存在（根据证件号）
            Passenger existing = passengerMapper.findByIdNumber(info.getIdNumber());

            if (existing != null) {
                log.debug("乘客已存在，idNumber={}", info.getIdNumber());
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
                log.debug("新增乘客，passengerId={}, idNumber={}", passenger.getPassengerId(), info.getIdNumber());
                passengers.add(passenger);
            }
        }

        log.info("乘客信息保存完成，数量: {}", passengers.size());
        return passengers;
    }

    /**
     * 生成订单号
     */
    private String generateBookingId() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String bookingId = "BK" + LocalDateTime.now().format(formatter) + String.format("%03d", (int)(Math.random() * 1000));
        log.debug("生成订单号: {}", bookingId);
        return bookingId;
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

    /**
     * 构建完整的订单响应（包含航班信息和乘客座位）
     */
    private BookingResponse buildBookingResponseWithData(
            Booking booking,
            FlightInstance flightInstance,
            List<Passenger> passengers,
            List<Seat> seats) {

        // 1. 构建航班信息
        BookingResponse.FlightInfo flightInfo = BookingResponse.FlightInfo.builder()
                .flightNumber(flightInstance.getFlightNumber())
                .airlineName(getAirlineName(flightInstance.getAirlineCode()))  // 需要查询航司名称
                .departAirport(flightInstance.getDepartAirport())
                .arrivalAirport(flightInstance.getArrivalAirport())
                .departDatetime(flightInstance.getDepartDatetime())
                .arrivalDatetime(flightInstance.getArrivalDatetime())
                .build();

        // 2. 构建乘客座位分配列表
        List<BookingResponse.PassengerSeatAssignment> passengerSeats = new ArrayList<>();
        for (int i = 0; i < passengers.size(); i++) {
            Passenger passenger = passengers.get(i);
            Seat seat = seats.get(i);

            passengerSeats.add(BookingResponse.PassengerSeatAssignment.builder()
                    .passengerName(passenger.getPassengerName())
                    .idNumber(passenger.getIdNumber())
                    .seatNumber(seat.getSeatNumber())
                    .seatType(seat.getSeatType().getDescription())
                    .build());
        }

        // 3. 构建完整响应
        return BookingResponse.builder()
                .bookingId(booking.getBookingId())
                .status(booking.getStatus())
                .flightInfo(flightInfo)
                .passengerSeats(passengerSeats)
                .totalOriginalPrice(booking.getTotalOriginalPrice())
                .discountAmount(booking.getDiscountAmount())
                .totalPrice(booking.getTotalPrice())
                .bookingTime(booking.getBookingTime())
                .holdExpireAt(booking.getHoldExpireAt())
                .build();
    }

    /**
     * 获取航司名称（从元数据库）
     */
    private String getAirlineName(String airlineCode) {
        try {
            DataSourceContextHolder.setDataSource("meta");
            Airline airline = airlineMapper.findByCode(airlineCode);
            return airline != null ? airline.getAirlineName() : airlineCode;
        } finally {
            DataSourceContextHolder.clearDataSource();
        }
    }

    /**
     * 构建完整的订单响应（用于幂等性检查返回已存在订单）
     */
    private BookingResponse buildCompleteBookingResponse(Booking booking, FlightInstance flightInstance) {
        // 切换到业务库查询乘客和座位信息
        String dataSource = getDataSourceByAirlineCode(flightInstance.getAirlineCode());

        try {
            DataSourceContextHolder.setDataSource(dataSource);

            // 查询乘客座位分配
            List<BookingPassengerSeat> bpsRecords = bookingPassengerSeatMapper.findByBookingId(booking.getBookingId());

            // 查询完整的乘客和座位信息
            List<BookingResponse.PassengerSeatAssignment> passengerSeats = new ArrayList<>();
            for (BookingPassengerSeat bps : bpsRecords) {
                Passenger passenger = passengerMapper.findById(bps.getPassengerId());

                passengerSeats.add(BookingResponse.PassengerSeatAssignment.builder()
                        .passengerName(passenger.getPassengerName())
                        .idNumber(passenger.getIdNumber())
                        .seatNumber(bps.getSeatNumber())
                        .seatType(bps.getSeatPreference())
                        .build());
            }

            // 构建航班信息
            BookingResponse.FlightInfo flightInfo = BookingResponse.FlightInfo.builder()
                    .flightNumber(flightInstance.getFlightNumber())
                    .airlineName(getAirlineName(flightInstance.getAirlineCode()))
                    .departAirport(flightInstance.getDepartAirport())
                    .arrivalAirport(flightInstance.getArrivalAirport())
                    .departDatetime(flightInstance.getDepartDatetime())
                    .arrivalDatetime(flightInstance.getArrivalDatetime())
                    .build();

            return BookingResponse.builder()
                    .bookingId(booking.getBookingId())
                    .status(booking.getStatus())
                    .flightInfo(flightInfo)
                    .passengerSeats(passengerSeats)
                    .totalOriginalPrice(booking.getTotalOriginalPrice())
                    .discountAmount(booking.getDiscountAmount())
                    .totalPrice(booking.getTotalPrice())
                    .bookingTime(booking.getBookingTime())
                    .holdExpireAt(booking.getHoldExpireAt())
                    .build();

        } finally {
            DataSourceContextHolder.clearDataSource();
        }
    }
}