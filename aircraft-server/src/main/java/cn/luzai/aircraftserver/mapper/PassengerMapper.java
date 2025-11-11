package cn.luzai.aircraftserver.mapper;

import cn.luzai.aircraftpojo.entity.core.Passenger;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 乘客 Mapper 接口
 */
@Mapper
public interface PassengerMapper {

    /**
     * 插入乘客信息
     *
     * @param passenger 乘客信息
     * @return 影响行数
     */
    int insert(Passenger passenger);

    /**
     * 批量插入乘客信息
     *
     * @param passengers 乘客列表
     * @return 影响行数
     */
    int batchInsert(@Param("passengers") List<Passenger> passengers);

    /**
     * 根据证件号查询乘客
     *
     * @param idNumber 证件号
     * @return 乘客信息
     */
    Passenger findByIdNumber(@Param("idNumber") String idNumber);
}
