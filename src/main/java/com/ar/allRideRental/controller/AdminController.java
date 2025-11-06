// AdminController.java
package com.ar.allRideRental.controller;

import com.ar.allRideRental.model.Booking;
import com.ar.allRideRental.model.User;
import com.ar.allRideRental.model.Vehicle;
import com.ar.allRideRental.model.VehicleAvailability;
import com.ar.allRideRental.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private VehicleAvailabilityService vehicleAvailabilityService;

    @Autowired
    private VehicleService  vehicleService;

    // Check if user is admin
    @GetMapping("/check-role/{phone}")
    public ResponseEntity<?> checkAdminRole(@PathVariable String phone) {
        try {
            User user = userService.getUserByPhone(phone);
            if (user == null) {
                return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", "User not found")
                );
            }

            boolean isAdmin = "admin".equalsIgnoreCase(user.getRole()) || Boolean.TRUE.equals(user.getIsAdmin());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("isAdmin", isAdmin);
            response.put("user", Map.of(
                "phone", user.getPhone(),
                "name", user.getName(),
                "role", user.getRole(),
                "email", user.getEmail()
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                Map.of("success", false, "message", "Error checking admin role: " + e.getMessage())
            );
        }
    }

    // Get all bookings (admin view)
    @GetMapping("/bookings")
    public ResponseEntity<?> getAllBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<Booking> bookings = adminService.getAllBookings(page, size);
            long totalBookings = adminService.getTotalBookingsCount();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", bookings);
            response.put("pagination", Map.of(
                "page", page,
                "size", size,
                "total", totalBookings
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                Map.of("success", false, "message", "Error fetching bookings: " + e.getMessage())
            );
        }
    }

    // Get upcoming bookings
    @GetMapping("/bookings/upcoming")
    public ResponseEntity<?> getUpcomingBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<Booking> upcomingBookings = adminService.getUpcomingBookings(page, size);
            long totalUpcoming = adminService.getUpcomingBookingsCount();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", upcomingBookings);
            response.put("pagination", Map.of(
                "page", page,
                "size", size,
                "total", totalUpcoming
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                Map.of("success", false, "message", "Error fetching upcoming bookings: " + e.getMessage())
            );
        }
    }

    // Get completed bookings
    @GetMapping("/bookings/completed")
    public ResponseEntity<?> getCompletedBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<Booking> completedBookings = adminService.getCompletedBookings(page, size);
            long totalCompleted = adminService.getCompletedBookingsCount();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", completedBookings);
            response.put("pagination", Map.of(
                "page", page,
                "size", size,
                "total", totalCompleted
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                Map.of("success", false, "message", "Error fetching completed bookings: " + e.getMessage())
            );
        }
    }

    // Update booking
    @PutMapping("/bookings/{bookingId}")
    public ResponseEntity<?> updateBooking(
            @PathVariable String bookingId,
            @RequestBody Map<String, Object> updates) {
        try {
            Booking updatedBooking = adminService.updateBooking(bookingId, updates);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Booking updated successfully");
            response.put("booking", updatedBooking);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                Map.of("success", false, "message", "Error updating booking: " + e.getMessage())
            );
        }
    }

    // Cancel booking
    @PutMapping("/bookings/{bookingId}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable String bookingId) {
        try {
            Booking cancelledBooking = adminService.cancelBooking(bookingId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Booking cancelled successfully");
            response.put("booking", cancelledBooking);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                Map.of("success", false, "message", "Error cancelling booking: " + e.getMessage())
            );
        }
    }

    // Set vehicle availability
    @PostMapping("/vehicles/{vehicleId}/availability")
    public ResponseEntity<?> setVehicleAvailability(
            @PathVariable String vehicleId,
            @RequestBody Map<String, Object> availabilityData) {
        try {
            VehicleAvailability availability = adminService.setVehicleAvailability(vehicleId, availabilityData);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Vehicle availability updated successfully");
            response.put("availability", availability);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                Map.of("success", false, "message", "Error setting vehicle availability: " + e.getMessage())
            );
        }
    }

    // Get vehicle availability
    @GetMapping("/vehicles/{vehicleId}/availability")
    public ResponseEntity<?> getVehicleAvailability(@PathVariable String vehicleId) {
        try {
            List<VehicleAvailability> availability = adminService.getVehicleAvailability(vehicleId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", availability);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                Map.of("success", false, "message", "Error fetching vehicle availability: " + e.getMessage())
            );
        }
    }

    // Get admin dashboard statistics
    @GetMapping("/stats")
    public ResponseEntity<?> getAdminStats() {
        try {
            Map<String, Object> stats = adminService.getAdminStats();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", stats);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                Map.of("success", false, "message", "Error fetching admin stats: " + e.getMessage())
            );
        }
    }

    // Set vehicle as unavailable
    @PostMapping("/vehicles/{vehicleId}/unavailable")
    public ResponseEntity<?> setVehicleUnavailable(
            @PathVariable String vehicleId,
            @RequestBody Map<String, Object> requestData) {
        try {
            LocalDate startDate = LocalDate.parse((String) requestData.get("startDate"));
            LocalDate endDate = LocalDate.parse((String) requestData.get("endDate"));
            String reason = (String) requestData.get("reason");

            // Validate no existing bookings in this period
            boolean hasBookings = bookingService.getBookingsByVehicleId(Long.parseLong(vehicleId)).stream()
                    .filter(booking -> !"cancelled".equals(booking.getStatus()))
                    .filter(booking -> overlaps(booking.getStartDate(), booking.getEndDate(), startDate, endDate))
                    .findAny()
                    .isPresent();

            if (hasBookings) {
                return ResponseEntity.badRequest().body(
                        Map.of("success", false, "message", "Cannot set unavailable: Vehicle has existing bookings in this period")
                );
            }

            VehicleAvailability availability = vehicleAvailabilityService
                    .setVehicleUnavailable(vehicleId, startDate, endDate, reason);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Vehicle marked as unavailable successfully");
            response.put("availability", availability);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", "Error setting vehicle unavailable: " + e.getMessage())
            );
        }
    }

    // Set vehicle as available (override unavailable periods)
    @PostMapping("/vehicles/{vehicleId}/available")
    public ResponseEntity<?> setVehicleAvailable(
            @PathVariable String vehicleId,
            @RequestBody Map<String, Object> requestData) {
        try {
            LocalDate startDate = LocalDate.parse((String) requestData.get("startDate"));
            LocalDate endDate = LocalDate.parse((String) requestData.get("endDate"));
            String reason = (String) requestData.get("reason");

            VehicleAvailability availability = vehicleAvailabilityService
                    .setVehicleAvailable(vehicleId, startDate, endDate, reason);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Vehicle marked as available successfully");
            response.put("availability", availability);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", "Error setting vehicle available: " + e.getMessage())
            );
        }
    }

    // Remove unavailable period
    @DeleteMapping("/availability/{availabilityId}")
    public ResponseEntity<?> removeUnavailablePeriod(@PathVariable Long availabilityId) {
        try {
            vehicleAvailabilityService.removeUnavailablePeriod(availabilityId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Unavailable period removed successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", "Error removing unavailable period: " + e.getMessage())
            );
        }
    }

    // Get complete availability status
    @GetMapping("/vehicles/{vehicleId}/availability-status")
    public ResponseEntity<?> getAvailabilityStatus(
            @PathVariable String vehicleId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            Map<String, Object> status = bookingService.getVehicleAvailabilityStatus(
                    Long.parseLong(vehicleId), startDate, endDate);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", status);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", "Error fetching availability status: " + e.getMessage())
            );
        }
    }

    @DeleteMapping("/vehicles/{vehicleId}/availability/clear-conflicts")
    public ResponseEntity<?> clearConflictingAvailability(@PathVariable String vehicleId) {
        try {
            // Get all availability records for the vehicle
            List<VehicleAvailability> allRecords = vehicleAvailabilityService.getAvailabilityByVehicleId(vehicleId);

            // Group by date range and keep only the most recent record for each range
            Map<String, VehicleAvailability> mostRecentByRange = new HashMap<>();
            List<VehicleAvailability> toDelete = new ArrayList<>();

            for (VehicleAvailability record : allRecords) {
                String rangeKey = record.getStartDate() + "_" + record.getEndDate();
                VehicleAvailability existing = mostRecentByRange.get(rangeKey);

                if (existing == null) {
                    mostRecentByRange.put(rangeKey, record);
                } else {
                    // Keep the most recent one, delete the older one
                    if (record.getCreatedAt().isAfter(existing.getCreatedAt())) {
                        toDelete.add(existing);
                        mostRecentByRange.put(rangeKey, record);
                    } else {
                        toDelete.add(record);
                    }
                }
            }

            // Delete conflicting records
            for (VehicleAvailability record : toDelete) {
                vehicleAvailabilityService.deleteAvailability(record.getId());
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cleared " + toDelete.size() + " conflicting availability records");
            response.put("remainingRecords", mostRecentByRange.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", "Error clearing conflicts: " + e.getMessage())
            );
        }
    }

    // Get available purpose options from existing vehicles
    @GetMapping("/vehicles/{vehicleId}/purpose-options")
    public ResponseEntity<?> getAvailablePurposeOptions(@PathVariable String vehicleId) {
        try {
            // Validate vehicle exists
            if (!vehicleService.vehicleExists(vehicleId)) {
                return ResponseEntity.badRequest().body(
                        Map.of("success", false, "message", "Vehicle not found with id: " + vehicleId)
                );
            }

            // Get purpose options from existing vehicles data
            List<Map<String, String>> purposeOptions = vehicleService.getAvailablePurposeOptions();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", purposeOptions);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", "Error fetching purpose options: " + e.getMessage())
            );
        }
    }

    // Update vehicle purpose
    @PutMapping("/vehicles/{vehicleId}/purpose")
    public ResponseEntity<?> updateVehiclePurpose(
            @PathVariable String vehicleId,
            @RequestBody Map<String, String> requestData) {
        try {
            String purpose = requestData.get("purpose");

            // Validate purpose
            if (!List.of("rent", "sale", "both").contains(purpose)) {
                return ResponseEntity.badRequest().body(
                        Map.of("success", false, "message", "Invalid purpose. Must be 'rent', 'sale', or 'both'")
                );
            }

            // Get the vehicle
            Vehicle vehicle = vehicleService.getVehicleById(Long.parseLong(vehicleId))
                    .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + vehicleId));

            // Update the purpose
            vehicle.setPurpose(purpose);
            Vehicle updatedVehicle = vehicleService.updateVehicle(Long.parseLong(vehicleId), vehicle);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Vehicle purpose updated successfully");
            response.put("vehicle", Map.of(
                    "id", updatedVehicle.getId(),
                    "name", updatedVehicle.getName(),
                    "purpose", updatedVehicle.getPurpose()
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", "Error updating vehicle purpose: " + e.getMessage())
            );
        }
    }

    private boolean overlaps(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        return !start1.isAfter(end2) && !start2.isAfter(end1);
    }
}