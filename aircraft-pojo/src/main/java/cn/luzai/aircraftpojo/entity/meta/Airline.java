package cn.luzai.aircraftpojo.entity.meta;

import cn.luzai.aircraftpojo.entity.enums.AirlineStatus;
import cn.luzai.aircraftpojo.entity.enums.DbType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 航空公司实体类
 * 对应表：airline-meta.airline
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Airline {

    /**
     * 航司二字码（IATA，主键）
     */
    private String airlineCode;

    /**
     * 航司名称
     */
    private String airlineName;

    /**
     * 同航司联程折扣率（0.05=5%折扣）
     */
    private BigDecimal ownDiscount;

    /**
     * 所属国家
     */
    private String country;

    /**
     * 对应业务库名（如airline-a）
     */
    private String dbName;

    /**
     * 数据库类型（MYSQL/ORACLE/POSTGRESQL）
     */
    private DbType dbType;

    /**
     * 航司状态（ACTIVE/INACTIVE）
     */
    private AirlineStatus status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
