package cn.luzai.aircraftpojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Seat {
    private String seatId;           // 座位ID（主键，格式：飞机ID_排号列号）
    private Long airplaneId;         // 所属飞机ID（外键）
    private Integer rowNum;          // 排号
    private Integer colNum;          // 列号
    private Boolean isAvailable;     // 是否可用（1=可用，0=不可用）
}
