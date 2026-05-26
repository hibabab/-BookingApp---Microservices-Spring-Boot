package com.hiba.booking.equipment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final EquipmentMapper equipmentMapper;

    public Equipment findOrCreate(EquipmentDto dto) {
        return equipmentRepository
                .findByTypeAndName(dto.type(), dto.name())
                .orElseGet(() -> equipmentRepository.save(equipmentMapper.toEntity(dto)));
    }


}

