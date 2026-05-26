package com.hiba.booking.room;

import com.hiba.booking.equipment.EquipmentDto;
import com.hiba.booking.equipment.EquipmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RoomMapper {

    private final EquipmentMapper equipmentMapper;

    // ==================== Entity → DTO ====================
    public RoomDto toDto(Room room) {
        if (room == null) return null;

        List<EquipmentDto> equipmentDtos = room.getEquipments() != null
                ? room.getEquipments().stream()
                .map(equipmentMapper::toDto)
                .collect(Collectors.toList())
                : List.of();

        return new RoomDto(
                room.getCategory(),
                room.getDescription(),
                room.getNightlyPrice(),
                room.getSpace(),
                room.getNumberOfBeds(),
                room.getView(),
                room.getMaxOccupancy(),
                room.getPhotos() != null ? room.getPhotos() : List.of(),
                equipmentDtos
        );
    }

    // ==================== DTO → Entity ====================
    public Room toEntity(RoomDto dto) {
        if (dto == null) return null;

        Room room = new Room();
        room.setCategory(dto.category());
        room.setDescription(dto.description());
        room.setNightlyPrice(dto.nightlyPrice());
        room.setSpace(dto.space());
        room.setNumberOfBeds(dto.numberOfBeds());
        room.setView(dto.view());
        room.setMaxOccupancy(dto.maxOccupancy());
        room.setPhotos(dto.photoUrls() != null ? dto.photoUrls() : List.of());

        // Equipments are handled in the service layer to avoid cascade issues
        return room;
    }

    // ==================== Bulk Mapping ====================
    public List<RoomDto> toDtoList(List<Room> rooms) {
        return rooms != null
                ? rooms.stream().map(this::toDto).collect(Collectors.toList())
                : List.of();
    }

    public List<Room> toEntityList(List<RoomDto> dtos) {
        return dtos != null
                ? dtos.stream().map(this::toEntity).collect(Collectors.toList())
                : List.of();
    }
}
