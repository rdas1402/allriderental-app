package com.ar.allRideRental.service;

import com.ar.allRideRental.model.Booking;
import com.ar.allRideRental.model.BookingRequest;
import com.ar.allRideRental.model.Vehicle;
import com.ar.allRideRental.repository.BookingRepository;
import com.ar.allRideRental.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    public List<Booking> getBookingsByCustomerEmail(String email) {
        return bookingRepository.findByCustomerEmail(email);
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

    // NEW: Get bookings by vehicle ID for date blocking
    public List<Booking> getBookingsByVehicleId(Long vehicleId) {
        // Return only confirmed and active bookings for date blocking
        return bookingRepository.findByVehicleIdAndStatus(vehicleId, "confirmed");
    }

    public Booking createBooking(BookingRequest bookingRequest) {
        // Check for overlapping bookings for the same vehicle
        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(
                bookingRequest.getVehicleId(),
                bookingRequest.getStartDate(),
                bookingRequest.getEndDate()
        );

        if (!overlappingBookings.isEmpty()) {
            throw new RuntimeException("Vehicle is already booked for the selected dates");
        }

        Booking booking = new Booking();
        booking.setVehicleId(bookingRequest.getVehicleId());
        booking.setVehicleName(bookingRequest.getVehicleName());
        booking.setCustomerPhone(bookingRequest.getCustomerPhone());
        booking.setCustomerName(bookingRequest.getCustomerName());
        booking.setCustomerEmail(bookingRequest.getCustomerEmail());
        booking.setStartDate(bookingRequest.getStartDate());
        booking.setEndDate(bookingRequest.getEndDate());
        booking.setPickupTime(bookingRequest.getPickupTime());
        booking.setDropoffTime(bookingRequest.getDropoffTime());
        booking.setPickupLocation(bookingRequest.getPickupLocation());
        booking.setAdditionalDriver(bookingRequest.isAdditionalDriver());
        booking.setInsurance(bookingRequest.getInsurance());
        booking.setTotalAmount(bookingRequest.getTotalAmount());
        booking.setVehicle(vehicleRepository.findById(bookingRequest.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + bookingRequest.getVehicleId())));
        booking.setStatus(bookingRequest.getStatus());
        booking.setVehicleImageUrl(booking.getVehicle().getImageUrl());

        return bookingRepository.save(booking);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Booking> getBookingsByCustomerPhone(String phone) {
        return bookingRepository.findByCustomerPhoneOrderByCreatedAtDesc(phone);
    }

    public Booking getBookingById(Long id) {
        Optional<Booking> booking = bookingRepository.findById(id);
        if (booking.isPresent()) {
            return booking.get();
        }
        throw new RuntimeException("Booking not found with id: " + id);
    }

    public Booking updateBookingStatus(Long id, String status) {
        Booking booking = getBookingById(id);
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }

    public Booking cancelBooking(Long id) {
        Booking booking = getBookingById(id);

        // Check if booking can be cancelled (at least 24 hours before start)
        if (booking.getStartDate().isBefore(LocalDate.now().plusDays(1))) {
            throw new RuntimeException("Booking can only be cancelled at least 24 hours before pickup");
        }

        booking.setStatus("cancelled");
        return bookingRepository.save(booking);
    }

    public Map<String, Object> getBookingStatistics() {
        long totalBookings = bookingRepository.count();
        long confirmedBookings = bookingRepository.countByStatus("confirmed");
        long activeBookings = bookingRepository.countByStatus("active");
        long completedBookings = bookingRepository.countByStatus("completed");
        long cancelledBookings = bookingRepository.countByStatus("cancelled");

        Double totalRevenue = bookingRepository.getTotalRevenue();

        return Map.of(
                "totalBookings", totalBookings,
                "confirmedBookings", confirmedBookings,
                "activeBookings", activeBookings,
                "completedBookings", completedBookings,
                "cancelledBookings", cancelledBookings,
                "totalRevenue", totalRevenue != null ? totalRevenue : 0.0
        );
    }

    public boolean isVehicleAvailable(Long vehicleId, LocalDate startDate, LocalDate endDate) {
        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(
                vehicleId, startDate, endDate
        );
        return overlappingBookings.isEmpty();
    }
}