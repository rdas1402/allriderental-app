package com.ar.allRideRental.repository;

import com.ar.allRideRental.model.Booking;
import com.ar.allRideRental.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByCustomerEmail(String customerEmail);
    List<Booking> findByCustomerPhoneOrderByCreatedAtDesc(String customerPhone);
    List<Booking> findAllByOrderByCreatedAtDesc();
    Long countByStatus(String status);

    // NEW: Find bookings by vehicle ID
    List<Booking> findByVehicleId(Long vehicleId);

    // NEW: Find confirmed bookings by vehicle ID for date blocking
    List<Booking> findByVehicleIdAndStatus(Long vehicleId, String status);

    // Find overlapping bookings for a vehicle
    @Query("SELECT b FROM Booking b WHERE b.vehicleId = :vehicleId " +
            "AND b.status IN ('confirmed', 'active') " +
            "AND ((b.startDate BETWEEN :startDate AND :endDate) OR " +
            "(b.endDate BETWEEN :startDate AND :endDate) OR " +
            "(b.startDate <= :startDate AND b.endDate >= :endDate))")
    List<Booking> findOverlappingBookings(@Param("vehicleId") Long vehicleId,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);

    // Get total revenue from completed bookings
    @Query("SELECT SUM(b.totalAmount) FROM Booking b WHERE b.status = 'completed'")
    Double getTotalRevenue();
}