package cn.luzai.aircraftserver.mapper;

import cn.luzai.aircraftpojo.entity.core.BookingSegment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单航段 Mapper 接口
 */
@Mapper
public interface BookingSegmentMapper {

    /**
     * 插入订单航段
     *
     * @param segment 航段信息
     * @return 影响行数
     */
    int insert(BookingSegment segment);

    /**
     * 根据订单号查询航段列表
     *
     * @param bookingId 订单号
     * @return 航段列表
     */
    List<BookingSegment> findByBookingId(@Param("bookingId") String bookingId);
}
