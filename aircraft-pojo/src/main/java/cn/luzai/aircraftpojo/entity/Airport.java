package cn.luzai.aircraftpojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Airport {
    private String airportCode;      // 机场编码（主键）
    private String airportName;      // 机场名称
    private String city;             // 所在城市
    private Integer connectionTime;  // 最小转机时间（分钟）
}