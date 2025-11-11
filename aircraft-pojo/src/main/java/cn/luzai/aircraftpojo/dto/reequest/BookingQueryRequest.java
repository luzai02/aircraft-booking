package cn.luzai.aircraftpojo.dto.reequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

/**
 * 订单查询请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingQueryRequest {

    /**
     * 用户手机号（用于查询订单）
     */
    private String phone;

    /**
     * 证件号（用于查询订单）
     */
    private String idNumber;

    /**
     * 订单状态筛选（可选）
     * HOLD - 待支付
     * CONFIRMED - 已确认
     * CANCELLED - 已取消
     * COMPLETED - 已完成
     */
    @Pattern(regexp = "^(HOLD|CONFIRMED|CANCELLED|COMPLETED)?$",
            message = "订单状态只能是HOLD/CONFIRMED/CANCELLED/COMPLETED")
    private String status;
}
