package cn.luzai.aircraftpojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    private String bookingId;        // 订单号（主键）
    private Long tripId;             // 行程ID（外键）
    private Long passengerId;        // 乘客ID（外键）
    private LocalDateTime bookingTime;// 预订时间
    private BigDecimal totalFare;    // 总金额
    private String status;           // 订单状态（PENDING/PAYED等）
    private String receiptUrl;       // 电子凭证链接
}
