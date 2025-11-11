package cn.luzai.aircraftserver.mapper;

import cn.luzai.aircraftpojo.dto.response.SeatMapResponse;
import cn.luzai.aircraftpojo.entity.core.Seat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 座位 Mapper 接口
 */
@Mapper
public interface SeatMapper {

    /**
     * 查询航班的座位图
     *
     * @param instanceId 航班实例ID
     * @return 座位列表
     */
    List<SeatMapResponse.SeatInfo> findSeatMapByInstanceId(@Param("instanceId") String instanceId);

    /**
     * 根据座位号列表查询座位（用于锁定）
     *
     * @param instanceId 航班实例ID
     * @param seatNumbers 座位号列表
     * @return 座位列表
     */
    List<Seat> findByInstanceIdAndSeatNumbers(
            @Param("instanceId") String instanceId,
            @Param("seatNumbers") List<String> seatNumbers
    );

    /**
     * 批量锁定座位（乐观锁）
     *
     * @param seatIds 座位ID列表
     * @param bookingId 订单号
     * @param lockExpireAt 锁定过期时间
     * @return 影响行数
     */
    int lockSeats(
            @Param("seatIds") List<Long> seatIds,
            @Param("bookingId") String bookingId,
            @Param("lockExpireAt") LocalDateTime lockExpireAt
    );

    /**
     * 确认座位（LOCKED → SOLD）
     *
     * @param seatIds 座位ID列表
     * @param passengerId 乘客ID
     * @return 影响行数
     */
    int confirmSeats(
            @Param("seatIds") List<Long> seatIds,
            @Param("passengerId") Long passengerId
    );

    /**
     * 释放座位（LOCKED → AVAILABLE）
     *
     * @param bookingId 订单号
     * @return 影响行数
     */
    int releaseSeats(@Param("bookingId") String bookingId);
}
