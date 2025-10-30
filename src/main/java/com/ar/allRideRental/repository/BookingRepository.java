package com.ar.allRideRental.repository;

import com.ar.allRideRental.model.Booking;
import com.ar.allRideRental.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByCustomerEmail(String customerEmail);
    List<Booking> findByStatus(String status);
    List<Booking> findByUser(User user);
    List<Booking> findByCustomerPhone(String customerPhone);
}