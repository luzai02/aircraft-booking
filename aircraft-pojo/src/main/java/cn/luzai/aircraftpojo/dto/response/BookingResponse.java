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
 * 订单创建响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {

    /**
     * 订单号
     */
    private String bookingId;

    /**
     * 订单状态
     */
    private BookingStatus status;

    /**
     * 航班信息
     */
    private FlightInfo flightInfo;

    /**
     * 乘客座位分配列表
     */
    private List<PassengerSeatAssignment> passengerSeats;

    /**
     * 原始总价
     */
    private BigDecimal totalOriginalPrice;

    /**
     * 折扣金额
     */
    private BigDecimal discountAmount;

    /**
     * 实付总价
     */
    private BigDecimal totalPrice;

    /**
     * 订单创建时间
     */
    private LocalDateTime bookingTime;

    /**
     * 支付截止时间（HOLD状态过期时间）
     */
    private LocalDateTime holdExpireAt;

    /**
     * 航班信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlightInfo {
        private String flightNumber;
        private String airlineName;
        private String departAirport;
        private String arrivalAirport;
        private LocalDateTime departDatetime;
        private LocalDateTime arrivalDatetime;
    }

    /**
     * 乘客座位分配
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PassengerSeatAssignment {
        private String passengerName;
        private String idNumber;
        private String seatNumber;
        private String seatType;
    }
}
