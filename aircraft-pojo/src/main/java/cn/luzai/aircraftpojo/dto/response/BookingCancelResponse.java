package cn.luzai.aircraftpojo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 订单取消响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingCancelResponse {

    /**
     * 订单号
     */
    private String bookingId;

    /**
     * 取消状态（SUCCESS/FAILED）
     */
    private String status;

    /**
     * 取消时间
     */
    private LocalDateTime cancelTime;

    /**
     * 消息
     */
    private String message;
}
