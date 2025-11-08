package cn.luzai.aircraftpojo.entity.core;

import cn.luzai.aircraftpojo.entity.enums.CheckInStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 订单-乘客-座位三维关联实体类
 * 对应表：airline-a/b/c.booking_passenger_seat
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingPassengerSeat {

    /**
     * 主键
     */
    private Long id;

    /**
     * 订单号
     */
    private String bookingId;

    /**
     * 关联哪个航段
     */
    private Long segmentId;

    /**
     * 乘客ID
     */
    private Long passengerId;

    /**
     * 分配的座位ID
     */
    private Long seatId;

    /**
     * 座位号（冗余，如1A）
     */
    private String seatNumber;

    /**
     * 乘客座位偏好（WINDOW/AISLE/MIDDLE/ANY）
     */
    private String seatPreference;

    /**
     * 值机状态（NOT_CHECKED/CHECKED）
     */
    private CheckInStatus checkInStatus;

    /**
     * 值机时间
     */
    private LocalDateTime checkInTime;
}
