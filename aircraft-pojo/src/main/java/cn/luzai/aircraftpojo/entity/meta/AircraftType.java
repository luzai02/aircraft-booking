package cn.luzai.aircraftpojo.entity.meta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 机型配置实体类
 * 对应表：airline-meta.aircraft_type
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AircraftType {

    /**
     * 机型代码（如B738，主键）
     */
    private String aircraftCode;

    /**
     * 机型名称（如波音737-800）
     */
    private String aircraftName;

    /**
     * 制造商（Boeing/Airbus）
     */
    private String manufacturer;

    /**
     * 座位总排数
     */
    private Integer totalRows;

    /**
     * 每排座位数（标准3-3布局为6）
     */
    private Integer seatsPerRow;

    /**
     * 总座位数
     */
    private Integer totalSeats;

    /**
     * 座位布局（如ABC-DEF）
     */
    private String seatLayout;
}
