package com.hiba.booking.room;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByHotelId(Long hotelId);

    @Query("SELECT r FROM Room r WHERE :date NOT MEMBER OF r.unavailableDates")
    List<Room> findAvailableRoomsByDate(@Param("date") LocalDate date);
}
