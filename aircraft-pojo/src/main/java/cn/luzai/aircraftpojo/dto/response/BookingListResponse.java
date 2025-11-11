package cn.luzai.aircraftpojo.dto.response;

import cn.luzai.aircraftpojo.entity.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单列表响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingListResponse {

    /**
     * 订单号
     */
    private String bookingId;

    /**
     * 订单状态
     */
    private BookingStatus status;

    /**
     * 航班号
     */
    private String flightNumber;

    /**
     * 航司名称
     */
    private String airlineName;

    /**
     * 出发机场代码
     */
    private String departAirport;

    /**
     * 出发机场名称
     */
    private String departAirportName;

    /**
     * 到达机场代码
     */
    private String arrivalAirport;

    /**
     * 到达机场名称
     */
    private String arrivalAirportName;

    /**
     * 起飞时间
     */
    private LocalDateTime departDatetime;

    /**
     * 到达时间
     */
    private LocalDateTime arrivalDatetime;

    /**
     * 乘客姓名列表
     */
    private List<String> passengerNames;

    /**
     * 订单总价
     */
    private BigDecimal totalPrice;

    /**
     * 订单创建时间
     */
    private LocalDateTime bookingTime;

    /**
     * 是否可取消（未出行的订单可取消）
     */
    private Boolean cancellable;

    private String airlineCode;
}