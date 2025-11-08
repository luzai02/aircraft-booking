package cn.luzai.aircraftpojo.entity.log;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 订单操作日志实体类
 * 对应表：airline-a/b/c.booking_log
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingLog {

    /**
     * 日志ID（自增主键）
     */
    private Long logId;

    /**
     * 订单号
     */
    private String bookingId;

    /**
     * 操作类型（CREATE/HOLD/CONFIRM/CANCEL/REFUND）
     */
    private String operation;

    /**
     * 变更前状态
     */
    private String oldStatus;

    /**
     * 变更后状态
     */
    private String newStatus;

    /**
     * 操作人（用户ID或SYSTEM）
     */
    private String operator;

    /**
     * 操作IP
     */
    private String operatorIp;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 日志时间
     */
    private LocalDateTime createTime;
}
