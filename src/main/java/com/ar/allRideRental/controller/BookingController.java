package com.ar.allRideRental.controller;

import com.ar.allRideRental.dto.BookingResponse;
import com.ar.allRideRental.model.Booking;
import com.ar.allRideRental.model.BookingRequest;
import com.ar.allRideRental.service.BookingService;
import com.ar.allRideRental.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    // Create a new booking
    @PostMapping
    public ResponseEntity<ApiResponse<Booking>> createBooking(@RequestBody BookingRequest bookingRequest) {
        try {
            Booking booking = bookingService.createBooking(bookingRequest);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Booking created successfully",
                    booking
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            false,
                            "Error creating booking: " + e.getMessage(),
                            null
                    ));
        }
    }

    // Get all bookings
    @GetMapping
    public ResponseEntity<ApiResponse<List<Booking>>> getAllBookings() {
        try {
            List<Booking> bookings = bookingService.getAllBookings();
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Bookings fetched successfully",
                    bookings
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            false,
                            "Error fetching bookings: " + e.getMessage(),
                            null
                    ));
        }
    }

    // Get bookings by customer phone
    @GetMapping("/customer/{phone}")
    public ResponseEntity<ApiResponse<List<Booking>>> getBookingsByCustomer(@PathVariable String phone) {
        try {
            List<Booking> bookings = bookingService.getBookingsByCustomerPhone(phone);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Customer bookings fetched successfully",
                    bookings
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            false,
                            "Error fetching customer bookings: " + e.getMessage(),
                            null
                    ));
        }
    }

    // UPDATED: Get bookings by vehicle ID for date blocking - now using BookingResponse
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getBookingsByVehicle(@PathVariable Long vehicleId) {
        try {
            List<Booking> bookings = bookingService.getBookingsByVehicleId(vehicleId);

            // Convert to BookingResponse to include image URLs
            List<BookingResponse> bookingResponses = bookings.stream()
                    .map(booking -> new BookingResponse(booking, booking.getVehicle().getImageUrl()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Bookings fetched successfully",
                    bookingResponses
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            false,
                            "Error fetching bookings: " + e.getMessage(),
                            null
                    ));
        }
    }

    // Get booking by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Booking>> getBookingById(@PathVariable Long id) {
        try {
            Booking booking = bookingService.getBookingById(id);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Booking fetched successfully",
                    booking
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(
                            false,
                            "Booking not found: " + e.getMessage(),
                            null
                    ));
        }
    }

    // Update booking status
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Booking>> updateBookingStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusUpdate) {
        try {
            String newStatus = statusUpdate.get("status");
            Booking booking = bookingService.updateBookingStatus(id, newStatus);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Booking status updated successfully",
                    booking
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            false,
                            "Error updating booking status: " + e.getMessage(),
                            null
                    ));
        }
    }

    // Cancel booking
    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Booking>> cancelBooking(@PathVariable Long id) {
        try {
            Booking booking = bookingService.cancelBooking(id);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Booking cancelled successfully",
                    booking
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            false,
                            "Error cancelling booking: " + e.getMessage(),
                            null
                    ));
        }
    }

    // Get booking statistics
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getBookingStats() {
        try {
            Map<String, Object> stats = bookingService.getBookingStatistics();
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Booking statistics fetched successfully",
                    stats
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            false,
                            "Error fetching booking statistics: " + e.getMessage(),
                            null
                    ));
        }
    }

    // Check vehicle availability
    @GetMapping("/availability")
    public ResponseEntity<ApiResponse<Boolean>> checkVehicleAvailability(
            @RequestParam Long vehicleId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        try {
            boolean isAvailable = bookingService.isVehicleAvailable(vehicleId, startDate, endDate);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Availability checked successfully",
                    isAvailable
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            false,
                            "Error checking availability: " + e.getMessage(),
                            null
                    ));
        }
    }

    // Inner class for standardized API response
    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;

        public ApiResponse(boolean success, String message, T data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        // Getters and setters
        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }
}