package cn.luzai.aircraftpojo.entity.meta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 座位偏好实体类
 * 对应表：airline-meta.seat_preference
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatPreference {

    /**
     * 偏好代码（主键，如WINDOW）
     */
    private String preferenceCode;

    /**
     * 偏好名称（如靠窗）
     */
    private String preferenceName;

    /**
     * 对应座位列（如A,F为窗，C,D为过道）
     */
    private String seatLetters;

    /**
     * 排序顺序
     */
    private Integer sortOrder;
}
