package cn.luzai.aircraftserver.controller;

import cn.luzai.aircraftpojo.dto.reequest.BookingRequest;
import cn.luzai.aircraftpojo.dto.response.BookingResponse;
import cn.luzai.aircraftpojo.dto.response.SeatMapResponse;
import cn.luzai.aircraftserver.server.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 订单 Controller
 */
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingService bookingService;

    /**
     * 查询座位图
     */
    @GetMapping("/seat-map/{instanceId}")
    public ResponseEntity<SeatMapResponse> getSeatMap(@PathVariable String instanceId) {
        SeatMapResponse seatMap = bookingService.getSeatMap(instanceId);
        return ResponseEntity.ok(seatMap);
    }

    /**
     * 创建订单
     */
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request) {
        BookingResponse response = bookingService.createBooking(request);
        return ResponseEntity.ok(response);
    }
}
