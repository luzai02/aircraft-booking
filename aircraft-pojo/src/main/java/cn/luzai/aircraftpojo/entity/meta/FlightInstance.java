package cn.luzai.aircraftpojo.entity.meta;

import cn.luzai.aircraftpojo.entity.enums.FlightStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 航班实例实体类（动态库存）
 * 对应表：airline-meta.flight_instance
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightInstance {

    /**
     * 实例ID（格式：CA1301_20250601，主键）
     */
    private String instanceId;

    /**
     * 关联的航班号
     */
    private String flightNumber;

    /**
     * 航司代码（冗余字段，加速查询）
     */
    private String airlineCode;

    /**
     * 执飞日期
     */
    private LocalDate flightDate;

    /**
     * 实际执飞机型（可临时调整）
     */
    private String aircraftCode;

    /**
     * 总座位数
     */
    private Integer totalSeats;

    /**
     * 剩余可售座位数
     */
    private Integer availableSeats;

    /**
     * 出发机场代码
     */
    private String departAirport;

    /**
     * 到达机场代码
     */
    private String arrivalAirport;

    /**
     * 实际起飞时间（东八区）
     */
    private LocalDateTime departDatetime;

    /**
     * 实际到达时间（东八区）
     */
    private LocalDateTime arrivalDatetime;

    /**
     * 当前价格（动态调价）
     */
    private BigDecimal price;

    /**
     * 航班实例状态（AVAILABLE/FULL/CANCELLED/DEPARTED）
     */
    private FlightStatus status;

    /**
     * 最后同步时间（用于缓存失效）
     */
    private LocalDateTime lastSyncTime;
}
