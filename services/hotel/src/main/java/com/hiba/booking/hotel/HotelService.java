package com.hiba.booking.hotel;

import com.hiba.booking.equipment.Equipment;
import com.hiba.booking.equipment.EquipmentService;
import com.hiba.booking.equipment.EquipmentDto;
import com.hiba.booking.room.RoomMapper;
import com.hiba.booking.room.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final HotelMapper hotelMapper;
    private final EquipmentService equipmentService;
    private final RoomMapper roomMapper;
    private final RoomService roomService;

    // ==================== CREATE ====================
    public HotelDto createHotel(HotelDto dto) {
        // 1. Convert DTO → Entity
        Hotel hotel = hotelMapper.toEntity(dto);

        // 2. Handle equipments (findOrCreate)
        if (dto.equipments() != null && !dto.equipments().isEmpty()) {
            List<Equipment> equipments = dto.equipments().stream()
                    .map(equipmentService::findOrCreate)
                    .collect(Collectors.toList());
            hotel.setEquipments(equipments);
        }

        // 3. Handle regulation
        if (dto.regulation() != null) {
            Regulation regulation = hotelMapper.regulationDtoToEntity(dto.regulation());
            regulation.setHotel(hotel);
            hotel.setRegulation(regulation);
        }

        // 4. Save hotel BEFORE creating rooms
        Hotel savedHotel = hotelRepository.save(hotel);

        // 5. Delegate room creation to RoomService
        if (dto.rooms() != null && !dto.rooms().isEmpty()) {
            dto.rooms().forEach(roomDto -> roomService.createRoom(roomDto, savedHotel.getId()));
        }

        // 6. Reload hotel with rooms
        Hotel hotelWithRooms = hotelRepository.findById(savedHotel.getId())
                .orElseThrow(() -> new RuntimeException("Hotel not found after creation"));

        // 7. Return DTO
        return hotelMapper.toDto(hotelWithRooms);
    }

    // ==================== READ ====================
    @Transactional(readOnly = true)
    public HotelDto getHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found with id: " + id));
        return hotelMapper.toDto(hotel);
    }

    @Transactional(readOnly = true)
    public List<HotelDto> getAllHotels() {
        return hotelRepository.findAll()
                .stream()
                .map(hotelMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HotelDto> getHotelsByCity(String city) {
        return hotelRepository.findByAddressCity(city)
                .stream()
                .map(hotelMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HotelDto> getHotelsByMinimumRating(Double minRating) {
        return hotelRepository.findByRatingGreaterThanEqual(minRating)
                .stream()
                .map(hotelMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HotelDto> getHotelsByStars(int stars) {
        return hotelRepository.findByStars(stars)
                .stream()
                .map(hotelMapper::toDto)
                .collect(Collectors.toList());
    }

    // ==================== UPDATE ====================
    @Transactional
    public HotelDto updateHotel(Long id, HotelDto dto) {
        Hotel existingHotel = hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found with id: " + id));

        // Update simple fields
        if (dto.name() != null) existingHotel.setName(dto.name());
        if (dto.hotelCategory() != null) existingHotel.setHotelCategory(dto.hotelCategory());
        if (dto.description() != null) existingHotel.setDescription(dto.description());
        if (dto.distanceFromCenter() != null) existingHotel.setDistanceFromCenter(dto.distanceFromCenter());
        if (dto.rating() != null) existingHotel.setRating(dto.rating());
        if (dto.breakfastIncluded() != null) existingHotel.setBreakfastIncluded(dto.breakfastIncluded());
        if (dto.stars() > 0) existingHotel.setStars(dto.stars());

        // Update targets
        if (dto.targets() != null) existingHotel.setTargets(new ArrayList<>(dto.targets()));

        // Update photos
        if (dto.photos() != null) existingHotel.setPhotos(new ArrayList<>(dto.photos()));

        // Update equipments
        if (dto.equipments() != null) {
            List<Equipment> equipments = dto.equipments().stream()
                    .map(equipmentService::findOrCreate)
                    .collect(Collectors.toList());
            existingHotel.setEquipments(equipments);
        }

        // Update regulation
        if (dto.regulation() != null) {
            if (existingHotel.getRegulation() != null) {
                Regulation reg = existingHotel.getRegulation();
                reg.setCheckInTime(dto.regulation().checkInTime());
                reg.setCheckOutTime(dto.regulation().checkOutTime());
                reg.setRefundable(dto.regulation().refundable());
                reg.setDescription(dto.regulation().description());
            } else {
                Regulation newReg = hotelMapper.regulationDtoToEntity(dto.regulation());
                newReg.setHotel(existingHotel);
                existingHotel.setRegulation(newReg);
            }
        }

        Hotel updatedHotel = hotelRepository.save(existingHotel);
        return hotelMapper.toDto(updatedHotel);
    }

    // ==================== PARTIAL UPDATE ====================
    @Transactional
    public HotelDto updateHotelRating(Long id, Double newRating) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found with id: " + id));

        hotel.setRating(newRating);
        Hotel updatedHotel = hotelRepository.save(hotel);
        return hotelMapper.toDto(updatedHotel);
    }

    @Transactional
    public HotelDto addEquipmentToHotel(Long hotelId, EquipmentDto equipmentDto) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found with id: " + hotelId));

        Equipment equipment = equipmentService.findOrCreate(equipmentDto);

        if (!hotel.getEquipments().contains(equipment)) {
            hotel.getEquipments().add(equipment);
            hotel = hotelRepository.save(hotel);
        }

        return hotelMapper.toDto(hotel);
    }

    @Transactional
    public HotelDto removeEquipmentFromHotel(Long hotelId, Long equipmentId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found with id: " + hotelId));

        hotel.getEquipments().removeIf(eq -> eq.getId().equals(equipmentId));
        hotel = hotelRepository.save(hotel);

        return hotelMapper.toDto(hotel);
    }

    // ==================== DELETE ====================
    @Transactional
    public void deleteHotel(Long id) {
        if (!hotelRepository.existsById(id)) {
            throw new RuntimeException("Hotel not found with id: " + id);
        }
        hotelRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllHotels() {
        hotelRepository.deleteAll();
    }

    // ==================== BUSINESS METHODS ====================
    @Transactional(readOnly = true)
    public List<HotelDto> searchHotels(String city, Integer minStars, Double minRating) {
        return hotelRepository.findAll()
                .stream()
                .filter(h -> city == null || h.getAddress().getCity().equalsIgnoreCase(city))
                .filter(h -> minStars == null || h.getStars() >= minStars)
                .filter(h -> minRating == null || h.getRating() >= minRating)
                .map(hotelMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long countHotelsByCity(String city) {
        return hotelRepository.countByAddressCity(city);
    }

    @Transactional(readOnly = true)
    public boolean hotelExists(Long id) {
        return hotelRepository.existsById(id);
    }
}
