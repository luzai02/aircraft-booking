package cn.luzai.aircraftpojo.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 座位状态枚举
 */
@Getter
public enum SeatStatus {
    AVAILABLE("AVAILABLE", "可用"),
    LOCKED("LOCKED", "已锁定"),
    SOLD("SOLD", "已售出"),
    BLOCKED("BLOCKED", "已封锁");

    @JsonValue
    private final String code;
    private final String description;

    SeatStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static SeatStatus fromCode(String code) {
        for (SeatStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown seat status: " + code);
    }
}
