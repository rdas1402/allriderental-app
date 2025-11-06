package com.ar.allRideRental.repository;

import com.ar.allRideRental.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhone(String phone);
    boolean existsByPhone(String phone);

    // If you want to count users with recent bookings
    @Query("SELECT COUNT(DISTINCT u) FROM User u JOIN Booking b ON u.phone = b.customerPhone WHERE b.bookingDate >= :sinceDate")
    long countActiveUsers(@Param("sinceDate") LocalDateTime sinceDate);
}