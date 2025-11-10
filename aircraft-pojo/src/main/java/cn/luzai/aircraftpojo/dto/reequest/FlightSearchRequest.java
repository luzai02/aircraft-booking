package cn.luzai.aircraftpojo.dto.reequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;

/**
 * 航班查询请求DTO
 */
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
    @Pattern(regexp = "^[A-Z]{3}$", message = "出发城市必须是3位大写字母")
    private String departCity;

    /**
     * 到达城市（机场三字码，如CAN）
     */
    @NotBlank(message = "到达城市不能为空")
    @Pattern(regexp = "^[A-Z]{3}$", message = "到达城市必须是3位大写字母")
    private String arrivalCity;

    /**
     * 出行日期（必须是今天或未来日期）
     */
    @NotNull(message = "出行日期不能为空")
    @FutureOrPresent(message = "出行日期不能早于今天")
    private LocalDate flightDate;

    /**
     * 出行人数（至少1人，最多9人）
     */
    @NotNull(message = "出行人数不能为空")
    @Min(value = 1, message = "出行人数至少为1人")
    @Max(value = 9, message = "出行人数最多为9人")
    private Integer passengerCount = 1;

    /**
     * 排序方式（PRICE/TIME，可选）
     * PRICE: 按价格升序排序（最低价格优先）
     * TIME: 按飞行时长升序排序（最短时间优先）
     * 不传或传空：默认按起飞时间升序排序
     */
    @Pattern(regexp = "^(PRICE|TIME)?$", message = "排序方式只能是PRICE或TIME")
    private String sortType;
}