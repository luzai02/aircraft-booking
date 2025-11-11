package cn.luzai.aircraftserver.mapper;

import cn.luzai.aircraftpojo.entity.core.Booking;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单 Mapper 接口
 */
@Mapper
public interface BookingMapper {

    /**
     * 插入订单
     *
     * @param booking 订单信息
     * @return 影响行数
     */
    int insert(Booking booking);

    /**
     * 根据订单号查询订单
     *
     * @param bookingId 订单号
     * @return 订单信息
     */
    Booking findByBookingId(@Param("bookingId") String bookingId);

    /**
     * 根据客户端Token查询订单（幂等性检查）
     *
     * @param clientToken 客户端Token
     * @return 订单信息
     */
    Booking findByClientToken(@Param("clientToken") String clientToken);

    /**
     * 更新订单状态
     *
     * @param bookingId 订单号
     * @param oldStatus 旧状态
     * @param newStatus 新状态
     * @return 影响行数
     */
    int updateStatus(
            @Param("bookingId") String bookingId,
            @Param("oldStatus") String oldStatus,
            @Param("newStatus") String newStatus
    );
}
