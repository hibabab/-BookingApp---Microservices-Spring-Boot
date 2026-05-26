package com.hiba.booking.room;

import com.hiba.booking.equipment.Equipment;
import com.hiba.booking.equipment.EquipmentService;
import com.hiba.booking.hotel.Hotel;
import com.hiba.booking.hotel.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final EquipmentService equipmentService;
    private final RoomMapper roomMapper;

    public List<RoomDto> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(roomMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<RoomDto> getRoomsByHotelId(Long hotelId) {
        return roomRepository.findByHotelId(hotelId).stream()
                .map(roomMapper::toDto)
                .collect(Collectors.toList());
    }

    public static List<LocalDate> getDatesBetween(LocalDate start, LocalDate end) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate current = start;
        while (!current.isAfter(end)) {
            dates.add(current);
            current = current.plusDays(1);
        }
        return dates;
    }

    public List<RoomDto> getAvailableRooms(LocalDate start, LocalDate end) {
        List<LocalDate> dates = getDatesBetween(start, end);
        List<Room> allRooms = roomRepository.findAll();
        List<Room> availableRooms = new ArrayList<>();

        for (Room room : allRooms) {
            boolean isAvailable = dates.stream().noneMatch(date -> room.getUnavailableDates().contains(date));
            if (isAvailable) availableRooms.add(room);
        }

        return availableRooms.stream()
                .map(roomMapper::toDto)
                .collect(Collectors.toList());
    }

    public boolean isRoomAvailable(Long roomId, LocalDate start, LocalDate end) {
        List<LocalDate> dates = getDatesBetween(start, end);
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + roomId));
        return dates.stream().noneMatch(date -> room.getUnavailableDates().contains(date));
    }

    public RoomBookingResponse reserveRoom(RoomBookingRequest request) {
        List<LocalDate> dates = getDatesBetween(request.checkIn().toLocalDate(), request.checkOut().toLocalDate());
        int numberOfNights = dates.size();

        if (!isRoomAvailable(request.roomId(), request.checkIn().toLocalDate(), request.checkOut().toLocalDate())) {
            throw new RuntimeException("Room is not available for these dates.");
        }

        Room room = roomRepository.findById(request.roomId())
                .orElseThrow(() -> new RuntimeException("Room not found."));

        int price = (int) (room.getNightlyPrice() * numberOfNights);
        room.getUnavailableDates().addAll(dates);
        roomRepository.save(room);

        return new RoomBookingResponse(room.getId(), request.checkIn(), request.checkOut(), price);
    }

    public RoomDto createRoom(RoomDto roomDto, long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found with id: " + hotelId));

        Room room = roomMapper.toEntity(roomDto);
        room.setHotel(hotel);

        if (roomDto.equipments() != null) {
            List<Equipment> equipments = roomDto.equipments().stream()
                    .map(equipmentService::findOrCreate)
                    .collect(Collectors.toList());
            room.setEquipments(equipments);
        }

        Room savedRoom = roomRepository.save(room);
        return roomMapper.toDto(savedRoom);
    }

    public RoomDto updateRoom(Long id, RoomDto roomDto) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));

        if (roomDto.category() != null) room.setCategory(roomDto.category());
        if (roomDto.description() != null) room.setDescription(roomDto.description());
        if (roomDto.nightlyPrice() != null) room.setNightlyPrice(roomDto.nightlyPrice());
        if (roomDto.space() != null) room.setSpace(roomDto.space());
        if (roomDto.numberOfBeds() != null) room.setNumberOfBeds(roomDto.numberOfBeds());
        if (roomDto.view() != null) room.setView(roomDto.view());
        if (roomDto.maxOccupancy() != null) room.setMaxOccupancy(roomDto.maxOccupancy());
        if (roomDto.photoUrls() != null) room.setPhotos(roomDto.photoUrls());

        return roomMapper.toDto(roomRepository.save(room));
    }

    public void deleteRoom(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new RuntimeException("Room not found with id: " + id);
        }
        roomRepository.deleteById(id);
    }

    public RoomDto updateRoomPrice(Long id, Double newPrice) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));
        room.setNightlyPrice(newPrice);
        return roomMapper.toDto(roomRepository.save(room));
    }
}
