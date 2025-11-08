package cn.luzai.aircraftpojo.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 航班状态枚举
 */
@Getter
public enum FlightStatus {
    ACTIVE("ACTIVE", "正常"),
    CANCELLED("CANCELLED", "已取消"),
    SUSPENDED("SUSPENDED", "已暂停"),
    AVAILABLE("AVAILABLE", "可预订"),
    FULL("FULL", "已满员"),
    DEPARTED("DEPARTED", "已起飞");

    @JsonValue
    private final String code;
    private final String description;

    FlightStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static FlightStatus fromCode(String code) {
        for (FlightStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown flight status: " + code);
    }
}
