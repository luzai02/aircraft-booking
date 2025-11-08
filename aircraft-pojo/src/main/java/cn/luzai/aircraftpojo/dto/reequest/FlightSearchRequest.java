package cn.luzai.aircraftpojo.dto.reequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 航班查询请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightSearchRequest {

    /**
     * 出发城市（机场三字码，如PEK）
     */
    @NotBlank(message = "出发城市不能为空")
    private String departCity;

    /**
     * 到达城市（机场三字码，如CAN）
     */
    @NotBlank(message = "到达城市不能为空")
    private String arrivalCity;

    /**
     * 出行日期
     */
    @NotNull(message = "出行日期不能为空")
    private LocalDate flightDate;

    /**
     * 出行人数（默认1人）
     */
    @Min(value = 1, message = "出行人数至少为1人")
    private Integer passengerCount = 1;

    /**
     * 排序方式（PRICE/TIME，可选）
     * 暂时不处理，后续实现排序时使用
     */
    private String sortType;
}