package cn.luzai.aircraftpojo.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 乘客类型枚举
 */
@Getter
public enum PassengerType {
    ADULT("ADULT", "成人"),
    CHILD("CHILD", "儿童"),
    INFANT("INFANT", "婴儿");

    @JsonValue
    private final String code;
    private final String description;

    PassengerType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static PassengerType fromCode(String code) {
        for (PassengerType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown passenger type: " + code);
    }
}