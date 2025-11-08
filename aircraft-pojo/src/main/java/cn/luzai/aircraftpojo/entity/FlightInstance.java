package cn.luzai.aircraftpojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightInstance {
    private String instanceId;       // 航班实例ID（主键，格式：航班号_执飞日期）
    private String flightNumber;     // 航班号（外键）
    private LocalDate departDate;    // 执飞日期
    private Integer availableSeats;  // 剩余座位数
    private Integer lockSeats;       // 已锁定座位数
}
