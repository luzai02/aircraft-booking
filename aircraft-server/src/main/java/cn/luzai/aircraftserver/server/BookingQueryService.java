package cn.luzai.aircraftserver.server;

import cn.luzai.aircraftpojo.dto.reequest.BookingQueryRequest;
import cn.luzai.aircraftpojo.dto.response.BookingListResponse;
import cn.luzai.aircraftpojo.entity.meta.Airline;
import cn.luzai.aircraftpojo.entity.meta.Airport;
import cn.luzai.aircraftserver.ddc.DataSourceContextHolder;
import cn.luzai.aircraftserver.exception.BusinessException;
import cn.luzai.aircraftserver.mapper.AirlineMapper;
import cn.luzai.aircraftserver.mapper.AirportMapper;
import cn.luzai.aircraftserver.mapper.BookingMapper;
import cn.luzai.aircraftserver.mapper.BookingPassengerSeatMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单查询服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookingQueryService {

    private final BookingMapper bookingMapper;
    private final BookingPassengerSeatMapper bookingPassengerSeatMapper;
    private final AirlineMapper airlineMapper;
    private final AirportMapper airportMapper;

    /**
     * 查询用户的订单列表（半年内）
     */
    public List<BookingListResponse> queryUserBookings(BookingQueryRequest request) {
        log.info("查询用户订单，phone={}, idNumber={}, status={}",
                request.getPhone(), request.getIdNumber(), request.getStatus());

        if ((request.getPhone() == null || request.getPhone().isEmpty()) &&
                (request.getIdNumber() == null || request.getIdNumber().isEmpty())) {
            throw new BusinessException("手机号和证件号至少提供一个");
        }

        // 查询最近6个月的订单
        LocalDateTime startTime = LocalDateTime.now().minusMonths(6);

        // 需要查询所有3个业务库的订单
        List<BookingListResponse> allBookings = new ArrayList<>();

        // 遍历3个业务库
        for (String dataSource : List.of("airline-a", "airline-b", "airline-c")) {
            try {
                DataSourceContextHolder.setDataSource(dataSource);
                log.debug("查询数据源: {}", dataSource);

                List<BookingListResponse> bookings = bookingMapper.findUserBookings(
                        request.getPhone(),
                        request.getIdNumber(),
                        request.getStatus(),
                        startTime
                );

                allBookings.addAll(bookings);

            } catch (Exception e) {
                log.error("查询数据源 {} 失败", dataSource, e);
            } finally {
                DataSourceContextHolder.clearDataSource();
            }
        }

        // 补充航司名称和机场名称
        enrichBookingInfo(allBookings);

        // 补充乘客姓名列表
        enrichPassengerNames(allBookings);

        // 按订单时间倒序排序
        allBookings.sort((a, b) -> b.getBookingTime().compareTo(a.getBookingTime()));

        log.info("查询到 {} 个订单", allBookings.size());
        return allBookings;
    }

    /**
     * 补充航司和机场名称（从元数据库查询）
     */
    private void enrichBookingInfo(List<BookingListResponse> bookings) {
        if (bookings.isEmpty()) {
            return;
        }

        try {
            DataSourceContextHolder.setDataSource("meta");

            // 查询所有航司信息（缓存）
            Map<String, String> airlineNameMap = airlineMapper.findAll().stream()
                    .collect(Collectors.toMap(
                            Airline::getAirlineCode,
                            Airline::getAirlineName
                    ));

            // 查询所有机场信息（缓存）
            Map<String, String> airportNameMap = airportMapper.findAll().stream()
                    .collect(Collectors.toMap(
                            Airport::getAirportCode,
                            Airport::getAirportName
                    ));

            // 填充名称
            for (BookingListResponse booking : bookings) {
                booking.setAirlineName(airlineNameMap.get(booking.getAirlineCode()));
                booking.setDepartAirportName(airportNameMap.get(booking.getDepartAirport()));
                booking.setArrivalAirportName(airportNameMap.get(booking.getArrivalAirport()));
            }

        } finally {
            DataSourceContextHolder.clearDataSource();
        }
    }

    /**
     * 补充乘客姓名列表（从业务库查询）
     */
    private void enrichPassengerNames(List<BookingListResponse> bookings) {
        for (BookingListResponse booking : bookings) {
            try {
                // 根据航司代码确定数据源
                String dataSource = getDataSourceByAirlineCode(booking.getAirlineCode());
                DataSourceContextHolder.setDataSource(dataSource);

                // 查询乘客姓名
                List<String> passengerNames = bookingPassengerSeatMapper
                        .findPassengerNamesByBookingId(booking.getBookingId());

                booking.setPassengerNames(passengerNames);

            } catch (Exception e) {
                log.error("查询订单 {} 的乘客信息失败", booking.getBookingId(), e);
                booking.setPassengerNames(List.of("查询失败"));
            } finally {
                DataSourceContextHolder.clearDataSource();
            }
        }
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
