package cn.luzai.aircraftserver.mapper;

import cn.luzai.aircraftpojo.entity.meta.Airport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 机场 Mapper 接口
 */
@Mapper
public interface AirportMapper {

    /**
     * 查询所有机场
     */
    @Select("SELECT airport_code, airport_name, city, country, " +
            "timezone, connection_time_min " +
            "FROM airport")
    List<Airport> findAll();
}
