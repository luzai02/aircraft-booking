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
public class Airline {
    private String airlineCode;      // 航司编码（主键）
    private String airlineName;      // 航司名称
    private BigDecimal ownDiscount;  // 同航司折扣比例
    private String country;          // 所属国家
    private String dbIdentifier;     // 对应数据库标识
    private LocalDateTime createTime;// 创建时间
}
