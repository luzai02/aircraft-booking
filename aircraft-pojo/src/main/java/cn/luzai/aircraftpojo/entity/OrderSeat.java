package cn.luzai.aircraftpojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSeat {
    private Long id;                 // 主键（自增）
    private String bookingId;        // 订单号（外键）
    private String instanceId;       // 航班实例ID（外键）
    private String seatId;           // 座位ID（外键）
    private String airlineCode;      // 航司编码
    private String status;           // 座位状态（SELECTED/BOOKED等）
    private LocalDateTime lockTime;  // 座位锁定时间
}
