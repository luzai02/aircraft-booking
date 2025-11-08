package cn.luzai.aircraftpojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripFlight {
    private Long id;                 // 关联ID（主键，自增）
    private Long tripId;             // 行程ID（外键）
    private String instanceId;       // 航班实例ID（外键）
    private String airlineCode;      // 航司编码
}