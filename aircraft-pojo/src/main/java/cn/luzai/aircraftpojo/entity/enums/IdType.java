package cn.luzai.aircraftpojo.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 证件类型枚举
 */
@Getter
public enum IdType {
    ID_CARD("ID_CARD", "身份证"),
    PASSPORT("PASSPORT", "护照"),
    OTHER("OTHER", "其他");

    @JsonValue
    private final String code;
    private final String description;

    IdType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static IdType fromCode(String code) {
        for (IdType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown id type: " + code);
    }
}
