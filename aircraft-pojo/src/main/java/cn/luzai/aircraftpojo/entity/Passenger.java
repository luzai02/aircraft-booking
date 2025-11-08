package cn.luzai.aircraftpojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Passenger {
    private Long passengerId;        // 乘客ID（主键，自增）
    private String passengerName;    // 乘客姓名
    private String passportNumber;   // 护照号（唯一）
    private String phone;            // 联系电话
    private LocalDateTime createTime;// 创建时间
}