package cn.luzai.aircraftpojo.dto.reequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 订单取消请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingCancelRequest {

    /**
     * 订单号
     */
    @NotBlank(message = "订单号不能为空")
    private String bookingId;

    /**
     * 取消原因（可选）
     */
    private String cancelReason;
}
