package cn.luzai.aircraftpojo.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 座位类型枚举
 */
@Getter
public enum SeatType {
    WINDOW("WINDOW", "靠窗"),
    AISLE("AISLE", "过道"),
    MIDDLE("MIDDLE", "中间");

    @JsonValue
    private final String code;
    private final String description;

    SeatType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static SeatType fromCode(String code) {
        for (SeatType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown seat type: " + code);
    }
}
