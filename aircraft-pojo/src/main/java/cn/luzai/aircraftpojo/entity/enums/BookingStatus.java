package cn.luzai.aircraftpojo.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 订单状态枚举
 */
@Getter
public enum BookingStatus {
    PENDING("PENDING", "待处理"),
    HOLD("HOLD", "已锁定"),
    CONFIRMED("CONFIRMED", "已确认"),
    CANCELLED("CANCELLED", "已取消"),
    REFUNDED("REFUNDED", "已退款"),
    COMPLETED("COMPLETED", "已完成");

    @JsonValue
    private final String code;
    private final String description;

    BookingStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static BookingStatus fromCode(String code) {
        for (BookingStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown booking status: " + code);
    }
}
