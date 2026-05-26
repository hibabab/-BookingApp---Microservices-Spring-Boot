package com.hiba.booking.room;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<List<RoomDto>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @GetMapping("/hotels/{hotelId}")
    public ResponseEntity<List<RoomDto>> getRoomsByHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(roomService.getRoomsByHotelId(hotelId));
    }

    @GetMapping("/available")
    public List<RoomDto> getAvailableRooms(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        return roomService.getAvailableRooms(start, end);
    }

    @PostMapping("/hotels/{hotelId}")
    public ResponseEntity<RoomDto> createRoom(@PathVariable Long hotelId, @RequestBody RoomDto roomDto) {
        RoomDto created = roomService.createRoom(roomDto, hotelId);
        URI location = URI.create(String.format("/api/v1/hotels/%d/rooms", hotelId));
        return ResponseEntity.created(location).body(created);
    }

    @PostMapping("/reserve")
    public RoomBookingResponse reserveRoom(@RequestBody RoomBookingRequest request) {
        return roomService.reserveRoom(request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomDto> updateRoom(@PathVariable Long id, @RequestBody RoomDto roomDto) {
        RoomDto updated = roomService.updateRoom(id, roomDto);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/price")
    public ResponseEntity<RoomDto> updateRoomPrice(@PathVariable Long id, @RequestParam Double newPrice) {
        RoomDto updated = roomService.updateRoomPrice(id, newPrice);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}
