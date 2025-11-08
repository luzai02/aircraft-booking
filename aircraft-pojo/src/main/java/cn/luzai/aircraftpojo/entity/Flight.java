package cn.luzai.aircraftpojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Flight {
    private String flightNumber;     // 航班号（主键）
    private String airlineCode;      // 所属航司编码（外键）
    private String departAirport;    // 出发机场编码（外键）
    private String destAirport;      // 到达机场编码（外键）
    private LocalTime departTime;    // 出发时间
    private LocalTime arrivalTime;   // 到达时间
    private BigDecimal baseFare;     // 基础票价
    private Long airplaneId;         // 执飞机型ID（外键）
}
