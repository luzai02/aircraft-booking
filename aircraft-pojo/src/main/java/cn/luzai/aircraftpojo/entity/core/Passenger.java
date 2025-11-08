package cn.luzai.aircraftpojo.entity.core;

import cn.luzai.aircraftpojo.entity.enums.IdType;
import cn.luzai.aircraftpojo.entity.enums.PassengerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 乘客信息实体类
 * 对应表：airline-a/b/c.passenger
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Passenger {

    /**
     * 乘客ID（自增主键）
     */
    private Long passengerId;

    /**
     * 乘客姓名
     */
    private String passengerName;

    /**
     * 证件类型（ID_CARD/PASSPORT/OTHER）
     */
    private IdType idType;

    /**
     * 证件号码
     */
    private String idNumber;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 乘客类型（ADULT/CHILD/INFANT）
     */
    private PassengerType passengerType;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
