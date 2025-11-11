package cn.luzai.aircraftpojo.dto.reequest;

import cn.luzai.aircraftpojo.entity.enums.IdType;
import cn.luzai.aircraftpojo.entity.enums.PassengerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 乘客信息DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PassengerInfo {

    /**
     * 乘客姓名
     */
    @NotBlank(message = "乘客姓名不能为空")
    private String passengerName;

    /**
     * 证件类型（ID_CARD/PASSPORT/OTHER）
     */
    @NotNull(message = "证件类型不能为空")
    private IdType idType;

    /**
     * 证件号码
     */
    @NotBlank(message = "证件号码不能为空")
    private String idNumber;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 邮箱（可选）
     */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 乘客类型（ADULT/CHILD/INFANT）
     */
    @NotNull(message = "乘客类型不能为空")
    private PassengerType passengerType = PassengerType.ADULT;

    /**
     * 座位偏好（WINDOW/AISLE/MIDDLE/ANY）
     */
    private String seatPreference = "ANY";
}
