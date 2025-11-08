package cn.luzai.aircraftserver;

import cn.luzai.aircraftpojo.dto.reequest.FlightSearchRequest;
import cn.luzai.aircraftpojo.dto.response.FlightSearchResponse;
import cn.luzai.aircraftserver.server.FlightSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
class FlightSearchServiceTest {

    @Autowired
    private FlightSearchService flightSearchService;

    @Test
    void testSearchFlights() {
        // 构建查询请求
        FlightSearchRequest request = FlightSearchRequest.builder()
                .departCity("PEK")
                .arrivalCity("CAN")
                .flightDate(LocalDate.now().plusDays(1))
                .passengerCount(2)
                .build();

        // 执行查询
        List<FlightSearchResponse> flights = flightSearchService.searchFlights(request);

        // 验证结果
        assertThat(flights).isNotEmpty();
        assertThat(flights).allMatch(f -> f.getAvailableSeats() >= 2);
        assertThat(flights).allMatch(f -> "PEK".equals(f.getDepartAirport()));
        assertThat(flights).allMatch(f -> "CAN".equals(f.getArrivalAirport()));

        // 打印结果
        flights.forEach(f -> {
            System.out.println("航班号: " + f.getFlightNumber());
            System.out.println("航司: " + f.getAirlineName());
            System.out.println("起飞时间: " + f.getDepartDatetime());
            System.out.println("到达时间: " + f.getArrivalDatetime());
            System.out.println("价格: " + f.getPrice());
            System.out.println("剩余座位: " + f.getAvailableSeats());
            System.out.println("---");
        });
    }
}
