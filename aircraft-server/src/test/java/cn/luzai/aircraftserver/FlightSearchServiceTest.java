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

    /**
     * 测试默认排序（按起飞时间）
     */
    @Test
    void testSearchFlights_DefaultSort() {
        FlightSearchRequest request = FlightSearchRequest.builder()
                .departCity("PEK")
                .arrivalCity("CAN")
                .flightDate(LocalDate.of(2025, 11, 28))
                .passengerCount(2)
                .build();

        List<FlightSearchResponse> flights = flightSearchService.searchFlights(request);

        assertThat(flights).isNotEmpty();
        assertThat(flights).hasSize(5);

        // 验证第一个航班是最早起飞的
        assertThat(flights.get(0).getFlightNumber()).isEqualTo("CZ3101");
        assertThat(flights.get(0).getDepartDatetime().getHour()).isEqualTo(7);
    }

    /**
     * 测试按价格升序排序
     */
    @Test
    void testSearchFlights_SortByPrice() {
        FlightSearchRequest request = FlightSearchRequest.builder()
                .departCity("PEK")
                .arrivalCity("CAN")
                .flightDate(LocalDate.of(2025, 11, 28))
                .passengerCount(2)
                .sortType("PRICE")
                .build();

        List<FlightSearchResponse> flights = flightSearchService.searchFlights(request);

        assertThat(flights).isNotEmpty();

        // 验证第一个航班是最低价
        assertThat(flights.get(0).getFlightNumber()).isEqualTo("CZ3101");
        assertThat(flights.get(0).getPrice()).isEqualTo(950.00);

        // 验证价格升序排列
        for (int i = 0; i < flights.size() - 1; i++) {
            assertThat(flights.get(i).getPrice())
                    .isLessThanOrEqualTo(flights.get(i + 1).getPrice());
        }
    }

    /**
     * 测试按飞行时长升序排序
     */
    @Test
    void testSearchFlights_SortByTime() {
        FlightSearchRequest request = FlightSearchRequest.builder()
                .departCity("PEK")
                .arrivalCity("CAN")
                .flightDate(LocalDate.of(2025, 11, 28))
                .passengerCount(2)
                .sortType("TIME")
                .build();

        List<FlightSearchResponse> flights = flightSearchService.searchFlights(request);

        assertThat(flights).isNotEmpty();

        // 验证第一个航班是最短时间
        assertThat(flights.get(0).getFlightNumber()).isEqualTo("CZ3101");
        assertThat(flights.get(0).getFlightDurationMin()).isEqualTo(180);

        // 验证时长升序排列
        for (int i = 0; i < flights.size() - 1; i++) {
            assertThat(flights.get(i).getFlightDurationMin())
                    .isLessThanOrEqualTo(flights.get(i + 1).getFlightDurationMin());
        }
    }
}