package booking.example.booking.room;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "hotel-service",
        url = "${application.config.hotel-url}"
)
public interface RoomClient {

    @PostMapping(value = "/reserve", consumes = "application/json", produces = "application/json")
    RoomBookingResponse reserveRoom(@RequestBody RoomBookingRequest request);
}
