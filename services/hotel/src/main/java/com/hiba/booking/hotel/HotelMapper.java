package com.hiba.booking.hotel;

import com.hiba.booking.equipment.Equipment;
import com.hiba.booking.equipment.EquipmentDto;
import com.hiba.booking.equipment.EquipmentMapper;
import com.hiba.booking.room.Room;
import com.hiba.booking.room.RoomDto;
import com.hiba.booking.room.RoomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HotelMapper {

    private final RoomMapper roomMapper;
    private final EquipmentMapper equipmentMapper;

    // ==================== Entity → DTO ====================
    public HotelDto toDto(Hotel hotel) {
        if (hotel == null) {
            return null;
        }

        return new HotelDto(
                hotel.getName(),
                hotel.getHotelCategory(),
                addressToDto(hotel.getAddress()),
                hotel.getDescription(),
                hotel.getDistanceFromCenter(),
                hotel.getRating(),
                hotel.getBreakfastIncluded(),
                hotel.getStars(),
                hotel.getTargets() != null ? new ArrayList<>(hotel.getTargets()) : new ArrayList<>(),
                hotel.getPhotos() != null ? new ArrayList<>(hotel.getPhotos()) : new ArrayList<>(),
                hotel.getRooms() != null
                        ? hotel.getRooms().stream().map(roomMapper::toDto).collect(Collectors.toList())
                        : new ArrayList<>(),
                regulationToDto(hotel.getRegulation()),
                hotel.getEquipments() != null
                        ? hotel.getEquipments().stream().map(equipmentMapper::toDto).collect(Collectors.toList())
                        : new ArrayList<>()
        );
    }

    // ==================== DTO → Entity ====================
    public Hotel toEntity(HotelDto dto) {
        if (dto == null) {
            return null;
        }

        Hotel hotel = new Hotel();
        hotel.setName(dto.name());
        hotel.setHotelCategory(dto.hotelCategory());
        hotel.setAddress(addressDtoToEntity(dto.address()));
        hotel.setDescription(dto.description());
        hotel.setDistanceFromCenter(dto.distanceFromCenter());
        hotel.setRating(dto.rating());
        hotel.setBreakfastIncluded(dto.breakfastIncluded());
        hotel.setStars(dto.stars());
        hotel.setTargets(dto.targets() != null ? new ArrayList<>(dto.targets()) : new ArrayList<>());
        hotel.setPhotos(dto.photos() != null ? new ArrayList<>(dto.photos()) : new ArrayList<>());

        // Rooms, regulation, and equipments are not mapped here
        // to avoid circular dependencies. These are handled in the service layer.

        return hotel;
    }

    // ==================== Address Mapping ====================
    public AddressDto addressToDto(Address address) {
        if (address == null) {
            return null;
        }
        return new AddressDto(
                address.getStreet(),
                address.getZipCode(),
                address.getCity()
        );
    }

    public Address addressDtoToEntity(AddressDto dto) {
        if (dto == null) {
            return null;
        }
        return new Address(
                dto.street(),
                dto.zipCode(),
                dto.city()
        );
    }

    // ==================== Regulation Mapping ====================
    public RegulationDto regulationToDto(Regulation regulation) {
        if (regulation == null) {
            return null;
        }
        return new RegulationDto(
                regulation.getId(),
                regulation.getCheckInTime(),
                regulation.getCheckOutTime(),
                regulation.getRefundable(),
                regulation.getDescription()
        );
    }

    public Regulation regulationDtoToEntity(RegulationDto dto) {
        if (dto == null) {
            return null;
        }
        Regulation regulation = new Regulation();
        regulation.setCheckInTime(dto.checkInTime());
        regulation.setCheckOutTime(dto.checkOutTime());
        regulation.setRefundable(dto.refundable());
        regulation.setDescription(dto.description());
        return regulation;
    }

    // ==================== Bulk Mapping ====================
    public List<HotelDto> toDtoList(List<Hotel> hotels) {
        if (hotels == null) {
            return new ArrayList<>();
        }
        return hotels.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<Hotel> toEntityList(List<HotelDto> dtos) {
        if (dtos == null) {
            return new ArrayList<>();
        }
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
