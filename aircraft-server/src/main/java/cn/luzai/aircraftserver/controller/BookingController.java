package cn.luzai.aircraftserver.controller;

import cn.luzai.aircraftpojo.dto.reequest.BookingCancelRequest;
import cn.luzai.aircraftpojo.dto.reequest.BookingQueryRequest;
import cn.luzai.aircraftpojo.dto.reequest.BookingRequest;
import cn.luzai.aircraftpojo.dto.response.BookingCancelResponse;
import cn.luzai.aircraftpojo.dto.response.BookingListResponse;
import cn.luzai.aircraftpojo.dto.response.BookingResponse;
import cn.luzai.aircraftpojo.dto.response.SeatMapResponse;
import cn.luzai.aircraftserver.server.BookingCancelService;
import cn.luzai.aircraftserver.server.BookingQueryService;
import cn.luzai.aircraftserver.server.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 订单 Controller
 */
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingService bookingService;
    private final BookingQueryService bookingQueryService;
    private final BookingCancelService bookingCancelService;

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

    /**
     * 查询用户订单列表（半年内）
     */
    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingListResponse>> queryUserBookings(
            @Valid BookingQueryRequest request) {

        List<BookingListResponse> bookings = bookingQueryService.queryUserBookings(request);
        return ResponseEntity.ok(bookings);
    }

    /**
     * 取消订单
     */
    @PostMapping("/cancel")
    public ResponseEntity<BookingCancelResponse> cancelBooking(
            @Valid @RequestBody BookingCancelRequest request) {

        BookingCancelResponse response = bookingCancelService.cancelBooking(request);
        return ResponseEntity.ok(response);
    }
}
