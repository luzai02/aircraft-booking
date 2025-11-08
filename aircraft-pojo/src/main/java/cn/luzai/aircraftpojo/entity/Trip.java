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
public class Trip {
    private Long tripId;             // 行程ID（主键，自增）
    private Long passengerId;        // 乘客ID（外键）
    private Integer passengerCount;  // 乘客人数
    private BigDecimal totalFare;    // 总金额（含折扣）
    private String sortType;         // 排序维度（PRICE/TIME）
    private LocalDateTime createTime;// 创建时间
}
