package cn.luzai.aircraftpojo.entity.meta;


import cn.luzai.aircraftpojo.entity.enums.FlightStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * 航班时刻表实体类
 * 对应表：airline-meta.flight
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Flight {

    /**
     * 航班号（主键）
     */
    private String flightNumber;

    /**
     * 所属航司代码
     */
    private String airlineCode;

    /**
     * 出发机场代码
     */
    private String departAirport;

    /**
     * 到达机场代码
     */
    private String arrivalAirport;

    /**
     * 计划起飞时间（本地时间）
     */
    private LocalTime departTime;

    /**
     * 计划到达时间（本地时间）
     */
    private LocalTime arrivalTime;

    /**
     * 飞行时长（分钟）
     */
    private Integer flightDurationMin;

    /**
     * 执飞机型代码
     */
    private String aircraftCode;

    /**
     * 经济舱基础价格
     */
    private BigDecimal basePrice;

    /**
     * 航班状态（ACTIVE/CANCELLED/SUSPENDED）
     */
    private FlightStatus status;
}