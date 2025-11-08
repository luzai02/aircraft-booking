package cn.luzai.aircraftpojo.entity.core;

import cn.luzai.aircraftpojo.entity.enums.SeatStatus;
import cn.luzai.aircraftpojo.entity.enums.SeatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 座位实体类
 * 对应表：airline-a/b/c.seat
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Seat {

    /**
     * 座位ID（自增主键）
     */
    private Long seatId;

    /**
     * 航班实例ID（如CA1301_20250601）
     */
    private String instanceId;

    /**
     * 排号（1-N）
     */
    private Integer rowNumber;

    /**
     * 座位列号（A/B/C/D/E/F）
     */
    private String seatLetter;

    /**
     * 座位号（如1A、23F）
     */
    private String seatNumber;

    /**
     * 座位类型（WINDOW/AISLE/MIDDLE）
     */
    private SeatType seatType;

    /**
     * 座位状态（AVAILABLE/LOCKED/SOLD/BLOCKED）
     */
    private SeatStatus status;

    /**
     * LOCKED状态过期时间（15分钟）
     */
    private LocalDateTime lockExpireAt;

    /**
     * 关联订单号
     */
    private String bookingId;

    /**
     * 分配给哪个乘客
     */
    private Long passengerId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
