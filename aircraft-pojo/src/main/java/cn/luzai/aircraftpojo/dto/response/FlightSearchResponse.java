package cn.luzai.aircraftpojo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 航班查询响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightSearchResponse {

    /**
     * 航班实例ID（如CA1301_20250601）
     */
    private String instanceId;

    /**
     * 航班号
     */
    private String flightNumber;

    /**
     * 航司代码
     */
    private String airlineCode;

    /**
     * 航司名称
     */
    private String airlineName;

    /**
     * 出发机场代码
     */
    private String departAirport;

    /**
     * 出发机场名称
     */
    private String departAirportName;

    /**
     * 到达机场代码
     */
    private String arrivalAirport;

    /**
     * 到达机场名称
     */
    private String arrivalAirportName;

    /**
     * 起飞时间
     */
    private LocalDateTime departDatetime;

    /**
     * 到达时间
     */
    private LocalDateTime arrivalDatetime;

    /**
     * 飞行时长（分钟）
     */
    private Integer flightDurationMin;

    /**
     * 当前价格
     */
    private BigDecimal price;

    /**
     * 剩余座位数
     */
    private Integer availableSeats;

    /**
     * 机型代码
     */
    private String aircraftCode;

    /**
     * 机型名称
     */
    private String aircraftName;
}
