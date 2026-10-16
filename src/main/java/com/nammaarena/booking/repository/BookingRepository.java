package com.nammaarena.booking.repository;

import com.nammaarena.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    List<Booking> findByTurfId(Long turfId);
    
    List<Booking> findByTurfIdAndSportAndBookingDate(Long turfId, String sport, LocalDate bookingDate);
    
    @Query(value = "SELECT b.* FROM bookings b JOIN turfs t ON b.turf_id = t.id WHERE t.owner_id = :ownerId", nativeQuery = true)
    List<Booking> findByOwnerId(@Param("ownerId") Long ownerId);
    
    @Query(value = "SELECT b.id, b.user_id, u.name as user_name, b.turf_id, t.name as turf_name, " +
           "b.sport, b.booking_date, b.start_time, b.end_time, b.duration, b.price, " +
           "b.status, b.payment_id, b.created_at " +
           "FROM bookings b " +
           "JOIN turfs t ON b.turf_id = t.id " +
           "JOIN users u ON b.user_id = u.id " +
           "WHERE t.owner_id = :ownerId", nativeQuery = true)
    List<Object[]> findOwnerBookingsWithDetails(@Param("ownerId") Long ownerId);
}