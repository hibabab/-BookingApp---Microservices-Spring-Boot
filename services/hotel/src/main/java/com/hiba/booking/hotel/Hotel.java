package com.hiba.booking.hotel;

import com.hiba.booking.equipment.Equipment;
import com.hiba.booking.comment.Comment;
import com.hiba.booking.room.Room;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "hotels")
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private HotelCategory hotelCategory;

    @Embedded
    private Address address;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Double distanceFromCenter;
    private Double rating;
    private Boolean breakfastIncluded;
    private int stars;

    @ElementCollection
    @CollectionTable(name = "hotel_targets", joinColumns = @JoinColumn(name = "hotel_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "target")
    private List<Target> targets;

    @ElementCollection
    @CollectionTable(name = "hotel_photos", joinColumns = @JoinColumn(name = "hotel_id"))
    @Column(name = "photo_url")
    private List<String> photos = new ArrayList<>();

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Room> rooms = new ArrayList<>();

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToOne(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private Regulation regulation;

    @ManyToMany
    @JoinTable(
            name = "hotel_equipments_mapping",
            joinColumns = @JoinColumn(name = "hotel_id"),
            inverseJoinColumns = @JoinColumn(name = "equipment_id")
    )
    private List<Equipment> equipments = new ArrayList<>();
}
