package cn.luzai.aircraftpojo.dto.reequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {

    /**
     * 航班实例ID（如 CA1301_20251128）
     */
    @NotBlank(message = "航班实例ID不能为空")
    private String instanceId;

    /**
     * 乘客信息列表（至少1人）
     */
    @NotEmpty(message = "乘客信息不能为空")
    @Valid
    private List<PassengerInfo> passengers;

    /**
     * 座位选择列表（座位号，如 ["1A", "1B"]）
     */
    @NotEmpty(message = "座位选择不能为空")
    @Size(min = 1, message = "至少选择1个座位")
    private List<String> seatNumbers;

    /**
     * 联系人索引（passengers 列表中的索引，从0开始）
     */
    @NotNull(message = "联系人索引不能为空")
    private Integer contactPassengerIndex = 0;

    /**
     * 客户端幂等Token（防重复提交）
     */
    @NotBlank(message = "客户端Token不能为空")
    private String clientToken;

    /**
     * 备注信息（可选）
     */
    private String remark;
}
