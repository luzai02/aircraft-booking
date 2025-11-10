package cn.luzai.aircraftpojo.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 排序类型枚举
 */
@Getter
public enum SortType {
    /**
     * 按价格升序排序（最低价格优先）
     */
    PRICE("PRICE", "按价格升序", "fi.price ASC"),

    /**
     * 按飞行时长升序排序（最短时间优先）
     */
    TIME("TIME", "按时长升序", "TIMESTAMPDIFF(MINUTE, fi.depart_datetime, fi.arrival_datetime) ASC"),

    /**
     * 默认按起飞时间升序排序
     */
    DEFAULT("DEFAULT", "按起飞时间升序", "fi.depart_datetime ASC");

    @JsonValue
    private final String code;
    private final String description;
    private final String orderByClause;

    SortType(String code, String description, String orderByClause) {
        this.code = code;
        this.description = description;
        this.orderByClause = orderByClause;
    }

    /**
     * 根据代码获取排序类型
     */
    public static SortType fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return DEFAULT;
        }
        for (SortType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return DEFAULT;
    }

    /**
     * 获取 ORDER BY 子句
     */
    public static String getOrderByClause(String code) {
        return fromCode(code).getOrderByClause();
    }
}
