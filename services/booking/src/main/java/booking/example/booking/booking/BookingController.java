package booking.example.booking.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final BookingRepository bookingRepository;


    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@RequestBody BookingRequest request) throws Exception {
        BookingResponse response = bookingService.createbooking(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }



}