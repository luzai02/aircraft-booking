package cn.luzai.aircraftpojo.entity.meta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 机场实体类
 * 对应表：airline-meta.airport
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Airport {

    /**
     * 机场三字码（IATA，主键）
     */
    private String airportCode;

    /**
     * 机场名称
     */
    private String airportName;

    /**
     * 所在城市
     */
    private String city;

    /**
     * 所属国家
     */
    private String country;

    /**
     * 时区（如Asia/Shanghai）
     */
    private String timezone;

    /**
     * 最小中转时间（分钟）
     */
    private Integer connectionTimeMin;
}
