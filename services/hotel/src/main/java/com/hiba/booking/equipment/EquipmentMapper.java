package com.hiba.booking.equipment;

import org.springframework.stereotype.Component;

@Component
public class EquipmentMapper {

    // Convert DTO -> Entity
    public Equipment toEntity( EquipmentDto dto) {
        Equipment equipment = new Equipment();
        equipment.setType(dto.type());
        equipment.setName(dto.name());

        if (dto.type() == EquipmentType.ROOM) {
            equipment.setRoomCategory(RoomEquipmentCategory.valueOf(dto.category()));
        } else {
            equipment.setHotelCategory(HotelEquipmentCategory.valueOf(dto.category()));
        }

        return equipment;
    }

    // Convert Entity -> DTO
    public  EquipmentDto toDto(Equipment equipment) {
        String category;

        if (equipment.getType() == EquipmentType.ROOM) {
            category = equipment.getRoomCategory().name();
        } else {
            category = equipment.getHotelCategory().name();
        }

        return new  EquipmentDto(
                equipment.getType(),
                category,
                equipment.getName()
        );
    }
}
