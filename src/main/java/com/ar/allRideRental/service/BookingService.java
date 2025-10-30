package com.ar.allRideRental.service;

import com.ar.allRideRental.model.Booking;
import com.ar.allRideRental.model.Vehicle;
import com.ar.allRideRental.repository.BookingRepository;
import com.ar.allRideRental.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
    
    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }
    
    public List<Booking> getBookingsByCustomerEmail(String email) {
        return bookingRepository.findByCustomerEmail(email);
    }
    
    public Booking createBooking(Booking booking) {
        // Check if vehicle exists and is available
        Vehicle vehicle = vehicleRepository.findById(booking.getVehicle().getId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + booking.getVehicle().getId()));
        
        if (!vehicle.isAvailable()) {
            throw new RuntimeException("Vehicle is not available for booking");
        }
        
        // Calculate total amount based on rental duration
        long days = java.time.Duration.between(booking.getStartDate(), booking.getEndDate()).toDays();
        double pricePerDay = extractPriceFromString(vehicle.getPrice());
        booking.setTotalAmount(pricePerDay * days);
        
        booking.setCreatedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());
        
        return bookingRepository.save(booking);
    }
    
    public Booking updateBookingStatus(Long id, String status) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
        
        booking.setStatus(status);
        booking.setUpdatedAt(LocalDateTime.now());
        
        return bookingRepository.save(booking);
    }
    
    public void cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
        
        booking.setStatus("CANCELLED");
        booking.setUpdatedAt(LocalDateTime.now());
        
        bookingRepository.save(booking);
    }
    
    private double extractPriceFromString(String priceString) {
        // Extract numeric value from price string like "₹4,500/day"
        try {
            String numericString = priceString.replaceAll("[^\\d.]", "");
            return Double.parseDouble(numericString);
        } catch (NumberFormatException e) {
            return 1000.0; // Default price if parsing fails
        }
    }
}