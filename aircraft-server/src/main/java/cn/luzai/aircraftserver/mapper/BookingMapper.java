package cn.luzai.aircraftserver.mapper;

import cn.luzai.aircraftpojo.dto.response.BookingListResponse;
import cn.luzai.aircraftpojo.entity.core.Booking;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单 Mapper 接口
 */
@Mapper
public interface BookingMapper {

    /**
     * 插入订单
     */
    int insert(Booking booking);

    /**
     * 根据订单号查询订单
     */
    Booking findByBookingId(@Param("bookingId") String bookingId);

    /**
     * 根据客户端Token查询订单（幂等性检查）
     */
    Booking findByClientToken(@Param("clientToken") String clientToken);

    /**
     * 更新订单状态
     */
    int updateStatus(
            @Param("bookingId") String bookingId,
            @Param("oldStatus") String oldStatus,
            @Param("newStatus") String newStatus
    );

    /**
     * 查询用户的订单列表（根据手机号或证件号）
     *
     * @param phone 手机号
     * @param idNumber 证件号
     * @param status 订单状态（可选）
     * @param startTime 开始时间（最近6个月）
     * @return 订单列表
     */
    List<BookingListResponse> findUserBookings(
            @Param("phone") String phone,
            @Param("idNumber") String idNumber,
            @Param("status") String status,
            @Param("startTime") LocalDateTime startTime
    );

    /**
     * 取消订单
     *
     * @param bookingId 订单号
     * @param cancelReason 取消原因
     * @param cancelTime 取消时间
     * @return 影响行数
     */
    int cancelBooking(
            @Param("bookingId") String bookingId,
            @Param("cancelReason") String cancelReason,
            @Param("cancelTime") LocalDateTime cancelTime
    );
}
