// AdminService.java
package com.ar.allRideRental.service;

import com.ar.allRideRental.model.Booking;
import com.ar.allRideRental.model.VehicleAvailability;
import com.ar.allRideRental.repository.BookingRepository;
import com.ar.allRideRental.repository.VehicleAvailabilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private VehicleAvailabilityService vehicleAvailabilityService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private VehicleAvailabilityRepository vehicleAvailabilityRepository;

    // Booking Management
    public List<Booking> getAllBookings(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookingService.getAllBookings(pageable);
    }

    public List<Booking> getUpcomingBookings(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookingService.getUpcomingBookings(pageable);
    }

    public List<Booking> getCompletedBookings(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookingService.getCompletedBookings(pageable);
    }

    public long getTotalBookingsCount() {
        return bookingService.getTotalBookingsCount();
    }

    public long getUpcomingBookingsCount() {
        return bookingService.getUpcomingBookingsCount();
    }

    public long getCompletedBookingsCount() {
        return bookingService.getCompletedBookingsCount();
    }

    public Booking updateBooking(String bookingId, Map<String, Object> updates) {
        Booking booking = bookingService.getBookingById(bookingId);
        if (booking == null) {
            throw new RuntimeException("Booking not found with id: " + bookingId);
        }

        // Update fields
        if (updates.containsKey("pickupDate")) {
            booking.setStartDate(LocalDate.parse((String) updates.get("pickupDate")));
        }
        if (updates.containsKey("dropoffDate")) {
            booking.setEndDate(LocalDate.parse((String) updates.get(("dropoffDate"))));
        }
        if (updates.containsKey("pickupTime")) {
            booking.setPickupTime(LocalTime.parse(updates.get("pickupTime").toString()));
        }
        if (updates.containsKey("dropoffTime")) {
            booking.setDropoffTime(LocalTime.parse(updates.get("dropoffTime").toString()));
        }
        if (updates.containsKey("pickupLocation")) {
            booking.setPickupLocation((String) updates.get("pickupLocation"));
        }
        if (updates.containsKey("status")) {
            booking.setStatus((String) updates.get("status"));
        }

        return bookingService.saveBooking(booking);
    }

    public Booking cancelBooking(String bookingId) {
        Booking booking = bookingService.getBookingById(bookingId);
        if (booking == null) {
            throw new RuntimeException("Booking not found with id: " + bookingId);
        }

        booking.setStatus("cancelled");
        booking.setUpdatedAt(LocalDateTime.now());
        
        return bookingService.saveBooking(booking);
    }

    // Vehicle Availability Management
    public VehicleAvailability setVehicleAvailability(String vehicleId, Map<String, Object> availabilityData) {
        LocalDate startDate = LocalDate.parse((String) availabilityData.get("startDate"));
        LocalDate endDate = LocalDate.parse((String) availabilityData.get("endDate"));
        Boolean isAvailable = (Boolean) availabilityData.get("isAvailable");
        String reason = (String) availabilityData.get("reason");

        // Validate vehicle exists
        if (!vehicleService.vehicleExists(vehicleId)) {
            throw new RuntimeException("Vehicle not found with id: " + vehicleId);
        }

        // Validate date range
        if (startDate.isAfter(endDate)) {
            throw new RuntimeException("Start date cannot be after end date");
        }

        // FIXED: Check for conflicts only when setting as UNAVAILABLE
        if (!isAvailable) {
            // Check for overlapping bookings
            boolean hasBookings = bookingRepository.existsActiveBookingInPeriod(
                    Long.parseLong(vehicleId), startDate, endDate);

            if (hasBookings) {
                throw new RuntimeException("Cannot set vehicle as unavailable: There are existing bookings in this period");
            }
        }

        // FIXED: Remove any conflicting availability records for the same period
        List<VehicleAvailability> conflictingRecords = vehicleAvailabilityRepository
                .findOverlappingAvailabilities(vehicleId, startDate, endDate);

        for (VehicleAvailability conflictingRecord : conflictingRecords) {
            // Only remove if the new record would override the existing one
            if (conflictingRecord.getStartDate().equals(startDate) &&
                    conflictingRecord.getEndDate().equals(endDate)) {
                vehicleAvailabilityRepository.delete(conflictingRecord);
            }
        }

        VehicleAvailability availability = new VehicleAvailability();
        availability.setVehicleId(vehicleId);
        availability.setStartDate(startDate);
        availability.setEndDate(endDate);
        availability.setAvailable(isAvailable);
        availability.setReason(reason);
        availability.setCreatedAt(LocalDateTime.now());
        availability.setUpdatedAt(LocalDateTime.now());

        return vehicleAvailabilityRepository.save(availability);
    }

    public List<VehicleAvailability> getVehicleAvailability(String vehicleId) {
        return vehicleAvailabilityService.getAvailabilityByVehicleId(vehicleId);
    }

    // Admin Statistics
    public Map<String, Object> getAdminStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Booking stats
        stats.put("totalBookings", bookingService.getTotalBookingsCount());
        stats.put("upcomingBookings", bookingService.getUpcomingBookingsCount());
        stats.put("completedBookings", bookingService.getCompletedBookingsCount());
        stats.put("cancelledBookings", bookingService.getCancelledBookingsCount());
        
        // Revenue stats (you'll need to implement this based on your business logic)
        stats.put("totalRevenue", calculateTotalRevenue());
        
        // Vehicle stats
        stats.put("totalVehicles", vehicleService.getTotalVehiclesCount());
        stats.put("availableVehicles", vehicleService.getAvailableVehiclesCount());
        stats.put("maintenanceVehicles", vehicleService.getMaintenanceVehiclesCount());
        
        // User stats
        stats.put("totalUsers", userService.getTotalUsersCount());
        stats.put("activeUsers", userService.getActiveUsersCount());

        return stats;
    }

    private double calculateTotalRevenue() {
        // Implement revenue calculation logic based on your business rules
        List<Booking> completedBookings = bookingService.getCompletedBookings();
        return completedBookings.stream()
                .mapToDouble(Booking::getTotalAmount)
                .sum();
    }
}