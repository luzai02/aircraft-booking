package cn.luzai.aircraftpojo.entity.core;

import cn.luzai.aircraftpojo.entity.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 座位预留实体类
 * 对应表：airline-a/b/c.seat_reservation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatReservation {

    /**
     * 预留ID（UUID）
     */
    private String reservationId;

    /**
     * 航班实例ID
     */
    private String instanceId;

    /**
     * 预留的座位ID列表（JSON数组，如[123,124]）
     */
    private List<Long> seatIds;

    /**
     * 预留座位数
     */
    private Integer seatCount;

    /**
     * 客户端幂等Token（防重）
     */
    private String clientToken;

    /**
     * 预留状态（HOLD/CONFIRMED/CANCELLED/EXPIRED）
     */
    private ReservationStatus status;

    /**
     * 预留过期时间（默认15分钟）
     */
    private LocalDateTime holdExpireAt;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}