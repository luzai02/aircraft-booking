package cn.luzai.aircraftpojo.entity.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单航段实体类
 * 对应表：airline-a/b/c.booking_segment
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingSegment {

    /**
     * 航段ID（自增主键）
     */
    private Long segmentId;

    /**
     * 订单号（外键）
     */
    private String bookingId;

    /**
     * 航段顺序（1,2,3...）
     */
    private Integer sequence;

    /**
     * 航班实例ID
     */
    private String instanceId;

    /**
     * 航班号
     */
    private String flightNumber;

    /**
     * 航司代码
     */
    private String airlineCode;

    /**
     * 出发机场
     */
    private String departAirport;

    /**
     * 到达机场
     */
    private String arrivalAirport;

    /**
     * 起飞时间（东八区）
     */
    private LocalDateTime departDatetime;

    /**
     * 到达时间（东八区）
     */
    private LocalDateTime arrivalDatetime;

    /**
     * 该航段座位数
     */
    private Integer seatCount;

    /**
     * 航段单价
     */
    private BigDecimal segmentPrice;

    /**
     * 关联的预留记录ID
     */
    private String reservationId;
}
