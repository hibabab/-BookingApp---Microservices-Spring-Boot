package com.hiba.booking.room;

import com.hiba.booking.equipment.Equipment;
import com.hiba.booking.hotel.Hotel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Category category;

    private String description;
    private Double nightlyPrice;
    private Double space;

    @ElementCollection
    @CollectionTable(name = "room_photos", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "photo_url")
    private List<String> photos = new ArrayList<>();

    private Integer numberOfBeds;

    @Enumerated(EnumType.STRING)
    private View view;

    private Integer maxOccupancy;  // renamed from numberOfPeople

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "room_room_equipment",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "equipment_id")
    )
    private List<Equipment> equipments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    @ElementCollection
    @CollectionTable(name = "room_unavailable_dates", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "unavailable_date")
    private Set<LocalDate> unavailableDates = new HashSet<>();
}
