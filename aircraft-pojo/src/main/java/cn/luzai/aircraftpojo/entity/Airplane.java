package cn.luzai.aircraftpojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Airplane {
    private Long airplaneId;         // 飞机ID（主键，自增）
    private String airplaneType;     // 机型
    private Integer seatCount;       // 总座位数
    private String airlineCode;      // 所属航司编码（外键）
    private Integer rowCount;        // 座位排数
    private Integer colCount;        // 每排座位列数
}