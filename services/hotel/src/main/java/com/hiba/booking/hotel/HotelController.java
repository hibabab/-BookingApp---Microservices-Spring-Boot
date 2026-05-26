package com.hiba.booking.hotel;

import com.hiba.booking.equipment.EquipmentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hotels")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HotelController {

    private final HotelService hotelService;

    // ==================== CREATE ====================
    @PostMapping
    public ResponseEntity<HotelDto> createHotel(@RequestBody HotelDto hotelDto) {
        HotelDto created = hotelService.createHotel(hotelDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ==================== READ ====================
    @GetMapping("/{id}")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable Long id) {
        HotelDto hotel = hotelService.getHotelById(id);
        return ResponseEntity.ok(hotel);
    }

    @GetMapping
    public ResponseEntity<List<HotelDto>> getAllHotels() {
        List<HotelDto> hotels = hotelService.getAllHotels();
        return ResponseEntity.ok(hotels);
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<HotelDto>> getHotelsByCity(@PathVariable String city) {
        List<HotelDto> hotels = hotelService.getHotelsByCity(city);
        return ResponseEntity.ok(hotels);
    }

    @GetMapping("/rating/{minRating}")
    public ResponseEntity<List<HotelDto>> getHotelsByMinimumRating(@PathVariable Double minRating) {
        List<HotelDto> hotels = hotelService.getHotelsByMinimumRating(minRating);
        return ResponseEntity.ok(hotels);
    }

    @GetMapping("/stars/{stars}")
    public ResponseEntity<List<HotelDto>> getHotelsByStars(@PathVariable int stars) {
        List<HotelDto> hotels = hotelService.getHotelsByStars(stars);
        return ResponseEntity.ok(hotels);
    }

    @GetMapping("/search")
    public ResponseEntity<List<HotelDto>> searchHotels(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer minStars,
            @RequestParam(required = false) Double minRating
    ) {
        List<HotelDto> hotels = hotelService.searchHotels(city, minStars, minRating);
        return ResponseEntity.ok(hotels);
    }

    // ==================== UPDATE ====================
    @PutMapping("/{id}")
    public ResponseEntity<HotelDto> updateHotel(
            @PathVariable Long id,
            @RequestBody HotelDto hotelDto
    ) {
        HotelDto updated = hotelService.updateHotel(id, hotelDto);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/rating")
    public ResponseEntity<HotelDto> updateHotelRating(
            @PathVariable Long id,
            @RequestParam Double rating
    ) {
        HotelDto updated = hotelService.updateHotelRating(id, rating);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{hotelId}/equipments")
    public ResponseEntity<HotelDto> addEquipment(
            @PathVariable Long hotelId,
            @RequestBody EquipmentDto equipmentDto
    ) {
        HotelDto updated = hotelService.addEquipmentToHotel(hotelId, equipmentDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{hotelId}/equipments/{equipmentId}")
    public ResponseEntity<HotelDto> removeEquipment(
            @PathVariable Long hotelId,
            @PathVariable Long equipmentId
    ) {
        HotelDto updated = hotelService.removeEquipmentFromHotel(hotelId, equipmentId);
        return ResponseEntity.ok(updated);
    }

    // ==================== DELETE ====================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHotel(@PathVariable Long id) {
        hotelService.deleteHotel(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllHotels() {
        hotelService.deleteAllHotels();
        return ResponseEntity.noContent().build();
    }

    // ==================== UTILITY ====================
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> hotelExists(@PathVariable Long id) {
        boolean exists = hotelService.hotelExists(id);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/count/city/{city}")
    public ResponseEntity<Long> countHotelsByCity(@PathVariable String city) {
        long count = hotelService.countHotelsByCity(city);
        return ResponseEntity.ok(count);
    }
}