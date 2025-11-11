package cn.luzai.aircraftserver.mapper;

import cn.luzai.aircraftpojo.entity.core.BookingPassengerSeat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单-乘客-座位关联 Mapper 接口
 */
@Mapper
public interface BookingPassengerSeatMapper {

    /**
     * 批量插入乘客座位关联
     *
     * @param records 关联记录列表
     * @return 影响行数
     */
    int batchInsert(@Param("records") List<BookingPassengerSeat> records);

    /**
     * 根据订单号查询乘客座位分配
     *
     * @param bookingId 订单号
     * @return 分配记录列表
     */
    List<BookingPassengerSeat> findByBookingId(@Param("bookingId") String bookingId);

    /**
     * 查询订单的乘客姓名列表
     *
     * @param bookingId 订单号
     * @return 乘客姓名列表
     */
    List<String> findPassengerNamesByBookingId(@Param("bookingId") String bookingId);
}
