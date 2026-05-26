package com.hiba.booking.equipment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "equipments")
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EquipmentType type; // ROOM or HOTEL

    @Enumerated(EnumType.STRING)
    private RoomEquipmentCategory roomCategory; // null if type = HOTEL

    @Enumerated(EnumType.STRING)
    private HotelEquipmentCategory hotelCategory; // null if type = ROOM

    private String name;
}
