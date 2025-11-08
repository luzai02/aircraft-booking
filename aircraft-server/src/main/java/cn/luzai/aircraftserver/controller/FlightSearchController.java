package cn.luzai.aircraftserver.controller;

import cn.luzai.aircraftpojo.dto.reequest.FlightSearchRequest;
import cn.luzai.aircraftpojo.dto.response.FlightSearchResponse;
import cn.luzai.aircraftserver.server.FlightSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 航班查询 Controller
 */
@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
public class FlightSearchController {

    private final FlightSearchService flightSearchService;

    /**
     * 查询航班
     *
     * @param request 查询请求
     * @return 航班列表
     */
    @PostMapping("/search")
    public ResponseEntity<List<FlightSearchResponse>> searchFlights(
            @Validated @RequestBody FlightSearchRequest request) {

        List<FlightSearchResponse> flights = flightSearchService.searchFlights(request);
        return ResponseEntity.ok(flights);
    }
}
