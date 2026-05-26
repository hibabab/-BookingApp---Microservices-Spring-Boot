package com.hiba.booking.hotel;

import com.hiba.booking.equipment.EquipmentDto;
import com.hiba.booking.room.RoomDto;

import java.util.List;

public record HotelDto(
        String name,
        HotelCategory hotelCategory,
        AddressDto address,
        String description,
        Double distanceFromCenter,
        Double rating,
        Boolean breakfastIncluded,
        int stars,
        List<Target> targets,
        List<String> photos,
        List<RoomDto> rooms,
        RegulationDto regulation,
        List<EquipmentDto> equipments
) {}
