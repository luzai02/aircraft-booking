package cn.luzai.aircraftpojo.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 数据库类型枚举
 */
@Getter
public enum DbType {
    MYSQL("MYSQL", "MySQL数据库"),
    ORACLE("ORACLE", "Oracle数据库"),
    POSTGRESQL("POSTGRESQL", "PostgreSQL数据库");

    @JsonValue
    private final String code;
    private final String description;

    DbType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static DbType fromCode(String code) {
        for (DbType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown db type: " + code);
    }
}
