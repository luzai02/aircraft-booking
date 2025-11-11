package cn.luzai.aircraftserver.mapper;

import cn.luzai.aircraftpojo.entity.meta.Airline;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 航司 Mapper 接口
 */
@Mapper
public interface AirlineMapper {

    /**
     * 根据航司代码查询航司信息
     */
    @Select("SELECT airline_code, airline_name, own_discount, country, " +
            "db_name, db_type, status, create_time " +
            "FROM airline WHERE airline_code = #{airlineCode}")
    Airline findByCode(@Param("airlineCode") String airlineCode);

    /**
     * 查询所有航司
     */
    @Select("SELECT airline_code, airline_name, own_discount, country, " +
            "db_name, db_type, status, create_time " +
            "FROM airline")
    List<Airline> findAll();
}
