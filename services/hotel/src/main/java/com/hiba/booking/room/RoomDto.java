package com.hiba.booking.room;

import com.hiba.booking.equipment.EquipmentDto;

import java.util.List;

/**
 * Data Transfer Object for Room entity
 */
public record RoomDto(
        Category category,
        String description,
        Double nightlyPrice,
        Double space,
        Integer numberOfBeds,
        View view,
        Integer maxOccupancy,
        List<String> photoUrls,
        List<EquipmentDto> equipments
) {}
