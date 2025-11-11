package cn.luzai.aircraftpojo.dto.response;

import cn.luzai.aircraftpojo.entity.enums.SeatStatus;
import cn.luzai.aircraftpojo.entity.enums.SeatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 座位图响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatMapResponse {

    /**
     * 航班实例ID
     */
    private String instanceId;

    /**
     * 总排数
     */
    private Integer totalRows;

    /**
     * 每排座位数
     */
    private Integer seatsPerRow;

    /**
     * 座位布局（如 ABC-DEF）
     */
    private String seatLayout;

    /**
     * 座位列表
     */
    private List<SeatInfo> seats;

    /**
     * 单个座位信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeatInfo {
        private Long seatId;
        private Integer rowNumber;
        private String seatLetter;
        private String seatNumber;
        private SeatType seatType;
        private SeatStatus status;
    }
}
