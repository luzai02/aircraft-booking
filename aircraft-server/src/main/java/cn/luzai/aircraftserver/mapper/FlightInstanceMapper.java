package cn.luzai.aircraftserver.mapper;

import cn.luzai.aircraftpojo.dto.response.FlightSearchResponse;
import cn.luzai.aircraftpojo.entity.meta.FlightInstance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 航班实例 Mapper 接口
 */
/**
 * 航班实例 Mapper 接口
 */
@Mapper
public interface FlightInstanceMapper {

    /**
     * 查询航班（支持动态排序）
     *
     * @param departAirport 出发机场代码
     * @param arrivalAirport 到达机场代码
     * @param flightDate 出行日期
     * @param passengerCount 出行人数
     * @param sortType 排序类型（PRICE/TIME/null）
     * @return 航班查询结果列表
     */
    List<FlightSearchResponse> searchFlights(
            @Param("departAirport") String departAirport,
            @Param("arrivalAirport") String arrivalAirport,
            @Param("flightDate") LocalDate flightDate,
            @Param("passengerCount") Integer passengerCount,
            @Param("sortType") String sortType
    );

    /**
     * 根据实例ID查询航班实例
     *
     * @param instanceId 航班实例ID
     * @return 航班实例
     */
    FlightInstance findByInstanceId(@Param("instanceId") String instanceId);

    /**
     * 更新剩余座位数（扣减）
     *
     * @param instanceId 航班实例ID
     * @param seatCount 需要扣减的座位数
     * @return 影响行数
     */
    int decreaseAvailableSeats(
            @Param("instanceId") String instanceId,
            @Param("seatCount") Integer seatCount
    );

    /**
     * 增加剩余座位数（恢复余座）
     *
     * @param instanceId 航班实例ID
     * @param seatCount 需要恢复的座位数
     * @return 影响行数
     */
    int increaseAvailableSeats(
            @Param("instanceId") String instanceId,
            @Param("seatCount") Integer seatCount
    );
}
