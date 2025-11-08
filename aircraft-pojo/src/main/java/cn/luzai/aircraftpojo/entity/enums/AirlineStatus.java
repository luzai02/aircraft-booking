package cn.luzai.aircraftpojo.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 航司状态枚举
 */
@Getter
public enum AirlineStatus {
    ACTIVE("ACTIVE", "激活"),
    INACTIVE("INACTIVE", "停用");

    @JsonValue
    private final String code;
    private final String description;

    AirlineStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static AirlineStatus fromCode(String code) {
        for (AirlineStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown airline status: " + code);
    }
}
