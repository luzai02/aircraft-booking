package cn.luzai.aircraftpojo.entity.core;

import cn.luzai.aircraftpojo.entity.enums.BookingStatus;
import cn.luzai.aircraftpojo.entity.enums.BookingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单主表实体类
 * 对应表：airline-a/b/c.booking
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    /**
     * 订单号（格式：BK+yyyyMMddHHmmss+随机数）
     */
    private String bookingId;

    /**
     * 订单类型（SINGLE/MULTI）
     */
    private BookingType bookingType;

    /**
     * 乘客人数
     */
    private Integer passengerCount;

    /**
     * 联系人乘客ID（主乘客）
     */
    private Long contactPassengerId;

    /**
     * 原始总价（折扣前）
     */
    private BigDecimal totalOriginalPrice;

    /**
     * 折扣金额
     */
    private BigDecimal discountAmount;

    /**
     * 实付总价
     */
    private BigDecimal totalPrice;

    /**
     * 币种（默认CNY）
     */
    private String currency;

    /**
     * 订单状态（PENDING/HOLD/CONFIRMED/CANCELLED/REFUNDED/COMPLETED）
     */
    private BookingStatus status;

    /**
     * HOLD状态过期时间
     */
    private LocalDateTime holdExpireAt;

    /**
     * 订单创建时间
     */
    private LocalDateTime bookingTime;

    /**
     * 支付时间
     */
    private LocalDateTime paymentTime;

    /**
     * 取消时间
     */
    private LocalDateTime cancelTime;

    /**
     * 客户端幂等Token
     */
    private String clientToken;

    /**
     * 备注信息
     */
    private String remark;
}
