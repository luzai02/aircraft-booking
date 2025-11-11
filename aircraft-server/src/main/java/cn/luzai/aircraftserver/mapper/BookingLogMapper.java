package cn.luzai.aircraftserver.mapper;

import cn.luzai.aircraftpojo.entity.log.BookingLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单日志 Mapper 接口
 */
@Mapper
public interface BookingLogMapper {

    /**
     * 插入订单操作日志
     *
     * @param log 日志信息
     * @return 影响行数
     */
    int insert(BookingLog log);

    /**
     * 根据订单号查询操作日志（新增）
     *
     * @param bookingId 订单号
     * @return 日志列表
     */
    List<BookingLog> findByBookingId(@Param("bookingId") String bookingId);
}
