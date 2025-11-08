package cn.luzai.aircraftpojo.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 预留状态枚举
 */
@Getter
public enum ReservationStatus {
    HOLD("HOLD", "已预留"),
    CONFIRMED("CONFIRMED", "已确认"),
    CANCELLED("CANCELLED", "已取消"),
    EXPIRED("EXPIRED", "已过期");

    @JsonValue
    private final String code;
    private final String description;

    ReservationStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static ReservationStatus fromCode(String code) {
        for (ReservationStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown reservation status: " + code);
    }
}
