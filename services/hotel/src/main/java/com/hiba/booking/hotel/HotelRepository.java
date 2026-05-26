package com.hiba.booking.hotel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    // ==================== Search by name ====================
    Optional<Hotel> findByName(String name);

    List<Hotel> findByNameContainingIgnoreCase(String name);

    // ==================== Search by location ====================
    List<Hotel> findByAddressCity(String city);

    List<Hotel> findByAddressCountry(String country);

    List<Hotel> findByAddressCityAndAddressCountry(String city, String country);

    long countByAddressCity(String city);

    // ==================== Search by category ====================
    List<Hotel> findByHotelCategory(HotelCategory category);

    // ==================== Search by stars ====================
    List<Hotel> findByStars(int stars);

    List<Hotel> findByStarsGreaterThanEqual(int minStars);

    // ==================== Search by rating ====================
    List<Hotel> findByRatingGreaterThanEqual(Double minRating);

    List<Hotel> findByRatingBetween(Double minRating, Double maxRating);

    // ==================== Search by breakfast ====================
    List<Hotel> findByBreakfastIncluded(Boolean breakfastIncluded);

    // ==================== Search by distance ====================
    List<Hotel> findByDistanceFromCenterLessThanEqual(Double maxDistance);
}
