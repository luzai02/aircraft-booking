package cn.luzai.aircraftserver.server;

import cn.luzai.aircraftpojo.dto.reequest.FlightSearchRequest;
import cn.luzai.aircraftpojo.dto.response.FlightSearchResponse;
import cn.luzai.aircraftserver.annotation.TargetDataSource;
import cn.luzai.aircraftserver.mapper.FlightInstanceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 航班查询服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FlightSearchService {

    private final FlightInstanceMapper flightInstanceMapper;

    /**
     * 查询航班（支持排序）
     *
     * @param request 查询请求
     * @return 航班列表
     */
    @TargetDataSource("meta")  // 使用元数据库
    public List<FlightSearchResponse> searchFlights(FlightSearchRequest request) {
        log.info("查询航班：出发地={}, 目的地={}, 日期={}, 人数={}, 排序方式={}",
                request.getDepartCity(),
                request.getArrivalCity(),
                request.getFlightDate(),
                request.getPassengerCount(),
                request.getSortType());

        // 调用 Mapper 查询（传入排序类型）
        List<FlightSearchResponse> flights = flightInstanceMapper.searchFlights(
                request.getDepartCity(),
                request.getArrivalCity(),
                request.getFlightDate(),
                request.getPassengerCount(),
                request.getSortType()  // 新增排序参数
        );

        log.info("查询到 {} 个航班", flights.size());
        return flights;
    }
}

