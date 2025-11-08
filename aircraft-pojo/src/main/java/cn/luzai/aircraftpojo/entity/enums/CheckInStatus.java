package cn.luzai.aircraftpojo.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 值机状态枚举
 */
@Getter
public enum CheckInStatus {
    NOT_CHECKED("NOT_CHECKED", "未值机"),
    CHECKED("CHECKED", "已值机");

    @JsonValue
    private final String code;
    private final String description;

    CheckInStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static CheckInStatus fromCode(String code) {
        for (CheckInStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown check-in status: " + code);
    }
}
