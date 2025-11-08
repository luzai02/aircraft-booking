package cn.luzai.aircraftpojo.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 订单类型枚举
 */
@Getter
public enum BookingType {
    SINGLE("SINGLE", "单程"),
    MULTI("MULTI", "联程");

    @JsonValue
    private final String code;
    private final String description;

    BookingType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static BookingType fromCode(String code) {
        for (BookingType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown booking type: " + code);
    }
}
