package com.ar.allRideRental.service;

import com.ar.allRideRental.dto.BookingResponse;
import com.ar.allRideRental.model.Booking;
import com.ar.allRideRental.model.BookingRequest;
import com.ar.allRideRental.model.Vehicle;
import com.ar.allRideRental.model.VehicleAvailability;
import com.ar.allRideRental.repository.BookingRepository;
import com.ar.allRideRental.repository.VehicleAvailabilityRepository;
import com.ar.allRideRental.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private VehicleAvailabilityRepository vehicleAvailabilityRepository;

    @Autowired
    private VehicleAvailabilityService vehicleAvailabilityService;

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
        Long vehicleId = bookingRequest.getVehicleId();
        LocalDate startDate = bookingRequest.getStartDate();
        LocalDate endDate = bookingRequest.getEndDate();

        // Comprehensive availability check
        if (!isVehicleAvailableForBooking(vehicleId, startDate, endDate)) {
            throw new RuntimeException("Vehicle is not available for the selected dates");
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

    // Comprehensive availability check for bookings
    public boolean isVehicleAvailableForBooking(Long vehicleId, LocalDate startDate, LocalDate endDate) {
        String vehicleIdStr = String.valueOf(vehicleId);

        // 1. Check for overlapping confirmed/active bookings
        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(vehicleId, startDate, endDate);
        if (!overlappingBookings.isEmpty()) {
            return false;
        }

        // 2. Check for manual unavailability periods
        boolean isManuallyUnavailable = vehicleAvailabilityRepository
                .existsUnavailablePeriod(vehicleIdStr, startDate, endDate);

        return !isManuallyUnavailable;
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

    // Cancel booking and make dates available again
    public Booking cancelBooking(Long id) {
        Booking booking = getBookingById(id);

        // Store the dates before cancellation for potential availability updates
        LocalDate startDate = booking.getStartDate();
        LocalDate endDate = booking.getEndDate();
        String vehicleId = String.valueOf(booking.getVehicleId());

        booking.setStatus("cancelled");
        booking.setUpdatedAt(LocalDateTime.now());

        Booking cancelledBooking = bookingRepository.save(booking);

        // Optional: You can add logic here to automatically remove any manual unavailability
        // that was created specifically for this booking
        // removeBookingUnavailability(vehicleId, startDate, endDate);

        return cancelledBooking;
    }

    // Get complete availability status for a vehicle
    public Map<String, Object> getVehicleAvailabilityStatus(Long vehicleId, LocalDate startDate, LocalDate endDate) {
        String vehicleIdStr = String.valueOf(vehicleId);
        Map<String, Object> status = new HashMap<>();

        // Get booked dates
        List<Booking> bookings = getBookingsByVehicleId(vehicleId);
        List<LocalDate> bookedDates = extractBookedDates(bookings, startDate, endDate);

        // Get manually unavailable dates
        List<LocalDate> manualUnavailableDates = vehicleAvailabilityService.getUnavailableDates(vehicleIdStr, startDate, endDate);

        // Combine all unavailable dates
        Set<LocalDate> allUnavailableDates = new HashSet<>();
        allUnavailableDates.addAll(bookedDates);
        allUnavailableDates.addAll(manualUnavailableDates);

        status.put("vehicleId", vehicleId);
        status.put("startDate", startDate);
        status.put("endDate", endDate);
        status.put("bookedDates", bookedDates);
        status.put("manualUnavailableDates", manualUnavailableDates);
        status.put("allUnavailableDates", new ArrayList<>(allUnavailableDates));
        status.put("isAvailable", allUnavailableDates.isEmpty());

        return status;
    }

    private List<LocalDate> extractBookedDates(List<Booking> bookings, LocalDate startRange, LocalDate endRange) {
        List<LocalDate> bookedDates = new ArrayList<>();

        for (Booking booking : bookings) {
            if ("cancelled".equalsIgnoreCase(booking.getStatus())) {
                continue; // Skip cancelled bookings
            }

            LocalDate current = booking.getStartDate();
            while (!current.isAfter(booking.getEndDate())) {
                if (!current.isBefore(startRange) && !current.isAfter(endRange)) {
                    bookedDates.add(current);
                }
                current = current.plusDays(1);
            }
        }

        return bookedDates;
    }

    // Check specific date availability
    public boolean isDateAvailable(Long vehicleId, LocalDate date) {
        String vehicleIdStr = String.valueOf(vehicleId);

        // Check bookings
        boolean hasBooking = bookingRepository.existsActiveBookingForDate(vehicleId, date);
        if (hasBooking) {
            return false;
        }

        // Check manual unavailability
        boolean isManuallyUnavailable = vehicleAvailabilityRepository
                .existsUnavailableForDate(vehicleIdStr, date);

        return !isManuallyUnavailable;
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

    public List<Booking> getAllBookings(Pageable pageable) {
        return bookingRepository.findAllByOrderByBookingDateDesc(pageable);
    }

    public List<Booking> getUpcomingBookings(Pageable pageable) {
        LocalDate today = LocalDate.now();
        return bookingRepository.findUpcomingBookings(today, pageable);
    }

    public List<Booking> getCompletedBookings(Pageable pageable) {
        return bookingRepository.findByStatusOrderByBookingDateDesc("completed", pageable);
    }

    public List<Booking> getCompletedBookings() {
        return bookingRepository.findByStatusOrderByBookingDateDesc("completed");
    }

    public List<Booking> getUpcomingBookings() {
        LocalDate today = LocalDate.now();
        return bookingRepository.findUpcomingBookings(today);
    }

    // Count methods
    public long getTotalBookingsCount() {
        return bookingRepository.count();
    }

    public long getUpcomingBookingsCount() {
        LocalDate today = LocalDate.now();
        return bookingRepository.countUpcomingBookings(today);
    }

    public long getCompletedBookingsCount() {
        return bookingRepository.countByStatus("completed");
    }

    public long getCancelledBookingsCount() {
        return bookingRepository.countByStatus("cancelled");
    }

    public Booking getBookingById(String bookingId) {
        return bookingRepository.findById(Long.valueOf(bookingId)).orElse(null);
    }

    public Booking saveBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    // Get all upcoming bookings for admin (all users)
    public List<Booking> getUpcomingBookingsForAdmin() {
        return getUpcomingBookings();
    }

    // Get all completed bookings for admin (all users)
    public List<Booking> getCompletedBookingsForAdmin() {
        return getCompletedBookings();
    }

    // Get all unavailable dates for a vehicle (from VehicleAvailability table)
    public List<LocalDate> getVehicleUnavailableDates(String vehicleId) {
        List<VehicleAvailability> unavailablePeriods = vehicleAvailabilityRepository.findUnavailablePeriods(vehicleId);
        List<LocalDate> unavailableDates = new ArrayList<>();

        for (VehicleAvailability period : unavailablePeriods) {
            // Only include future or current unavailable dates
            if (!period.getEndDate().isBefore(LocalDate.now())) {
                LocalDate currentDate = period.getStartDate();
                while (!currentDate.isAfter(period.getEndDate())) {
                    if (!currentDate.isBefore(LocalDate.now())) {
                        unavailableDates.add(currentDate);
                    }
                    currentDate = currentDate.plusDays(1);
                }
            }
        }

        return unavailableDates;
    }

    // Check if vehicle is available considering both bookings and manual unavailability
    public boolean isVehicleAvailable(Long vehicleId, LocalDate startDate, LocalDate endDate) {
        String vehicleIdStr = String.valueOf(vehicleId);

        // Check for overlapping bookings
        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(vehicleId, startDate, endDate);
        if (!overlappingBookings.isEmpty()) {
            return false;
        }

        // Check for manual unavailability periods
        boolean isManuallyUnavailable = vehicleAvailabilityRepository.isVehicleUnavailableForPeriod(
                vehicleIdStr, startDate, endDate);

        return !isManuallyUnavailable;
    }

    // Get comprehensive availability data for a vehicle
    public Map<String, Object> getVehicleAvailabilityData(Long vehicleId) {
        String vehicleIdStr = String.valueOf(vehicleId);
        Map<String, Object> availabilityData = new HashMap<>();

        // Get existing bookings
        List<Booking> bookings = getBookingsByVehicleId(vehicleId);
        List<BookingResponse> bookingResponses = bookings.stream()
                .map(booking -> new BookingResponse(booking, booking.getVehicle().getImageUrl()))
                .collect(Collectors.toList());

        // Get ALL availability records
        List<VehicleAvailability> allAvailability = vehicleAvailabilityRepository.findByVehicleIdOrderByStartDateDesc(vehicleIdStr);

        // Separate available and unavailable periods - FIXED LOGIC
        List<Map<String, Object>> availablePeriods = allAvailability.stream()
                .filter(avail -> avail.getAvailable() != null && avail.getAvailable()) // Only truly available periods
                .map(period -> {
                    Map<String, Object> periodData = new HashMap<>();
                    periodData.put("startDate", period.getStartDate());
                    periodData.put("endDate", period.getEndDate());
                    periodData.put("reason", period.getReason());
                    periodData.put("createdAt", period.getCreatedAt());
                    return periodData;
                })
                .collect(Collectors.toList());

        List<Map<String, Object>> unavailablePeriods = allAvailability.stream()
                .filter(avail -> avail.getAvailable() != null && !avail.getAvailable()) // Only truly unavailable periods
                .map(period -> {
                    Map<String, Object> periodData = new HashMap<>();
                    periodData.put("startDate", period.getStartDate());
                    periodData.put("endDate", period.getEndDate());
                    periodData.put("reason", period.getReason());
                    periodData.put("createdAt", period.getCreatedAt());
                    return periodData;
                })
                .collect(Collectors.toList());

        // Get flat list of unavailable dates for calendar display
        List<LocalDate> unavailableDates = getVehicleUnavailableDates(vehicleIdStr);

        // Fix: Check general availability correctly
        boolean isGenerallyAvailable = unavailablePeriods.isEmpty() ||
                unavailablePeriods.stream().noneMatch(period -> {
                    LocalDate periodStart = LocalDate.parse(period.get("startDate").toString());
                    LocalDate periodEnd = LocalDate.parse(period.get("endDate").toString());
                    LocalDate today = LocalDate.now();
                    return !periodEnd.isBefore(today); // Check if any unavailable period includes today or future
                });

        availabilityData.put("bookings", bookingResponses);
        availabilityData.put("unavailableDates", unavailableDates);
        availabilityData.put("availablePeriods", availablePeriods);
        availabilityData.put("unavailablePeriods", unavailablePeriods);
        availabilityData.put("isVehicleGenerallyAvailable", isGenerallyAvailable);

        return availabilityData;
    }

    public boolean isVehicleAvailableForDates(String vehicleId, LocalDate startDate, LocalDate endDate) {
        Long vehicleIdLong = Long.parseLong(vehicleId);

        System.out.println("=== AVAILABILITY CHECK ===");
        System.out.println("Vehicle: " + vehicleId + ", Dates: " + startDate + " to " + endDate);

        // 1. Check for overlapping active bookings
        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(vehicleIdLong, startDate, endDate);
        if (!overlappingBookings.isEmpty()) {
            System.out.println("❌ UNAVAILABLE: Found overlapping bookings");
            return false;
        }

        // 2. Check availability records - FIXED LOGIC
        List<VehicleAvailability> allAvailabilities = vehicleAvailabilityRepository
                .findOverlappingAvailabilities(vehicleId, startDate, endDate);

        System.out.println("Found " + allAvailabilities.size() + " overlapping availability records");

        if (!allAvailabilities.isEmpty()) {
            // Group by date range and get the most recent record for each conflicting period
            Map<String, VehicleAvailability> mostRecentByRange = new HashMap<>();

            for (VehicleAvailability availability : allAvailabilities) {
                String rangeKey = availability.getStartDate() + "_" + availability.getEndDate();
                VehicleAvailability existing = mostRecentByRange.get(rangeKey);

                if (existing == null || availability.getCreatedAt().isAfter(existing.getCreatedAt())) {
                    mostRecentByRange.put(rangeKey, availability);
                }
            }

            // Check if any of the most recent records mark the vehicle as unavailable
            for (VehicleAvailability availability : mostRecentByRange.values()) {
                if (!availability.getAvailable()) {
                    System.out.println("❌ UNAVAILABLE: Found explicit unavailable period - " +
                            availability.getStartDate() + " to " + availability.getEndDate() +
                            " Reason: " + availability.getReason());
                    return false;
                }
            }

            // If we have explicit available periods that cover the entire range, return true
            boolean hasExplicitAvailable = mostRecentByRange.values().stream()
                    .anyMatch(VehicleAvailability::getAvailable);

            if (hasExplicitAvailable) {
                System.out.println("✅ AVAILABLE: Explicit available period found");
                return true;
            }
        }

        // 3. Default to checking if vehicle is generally available (no current unavailable periods)
        boolean defaultAvailable = isVehicleGenerallyAvailable(vehicleIdLong);
        System.out.println("Using general availability: " + defaultAvailable);
        return defaultAvailable;
    }

    private boolean isDateRangeFullyCoveredByAvailablePeriods(LocalDate startDate, LocalDate endDate,
                                                              List<VehicleAvailability> availabilities) {
        // Get only available periods
        List<VehicleAvailability> availablePeriods = availabilities.stream()
                .filter(VehicleAvailability::getAvailable)
                .collect(Collectors.toList());

        // Sort by start date
        availablePeriods.sort(Comparator.comparing(VehicleAvailability::getStartDate));

        // Check if the available periods fully cover the requested range
        LocalDate current = startDate;

        for (VehicleAvailability period : availablePeriods) {
            if (current.isBefore(period.getStartDate())) {
                // Gap found
                return false;
            }
            if (current.isBefore(period.getEndDate()) || current.equals(period.getEndDate())) {
                current = period.getEndDate().plusDays(1);
                if (current.isAfter(endDate)) {
                    return true;
                }
            }
        }

        return current.isAfter(endDate);
    }

    private boolean overlaps(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        return !start1.isAfter(end2) && !start2.isAfter(end1);
    }

    public boolean isVehicleGenerallyAvailable(Long vehicleId) {
        // Check if there are any current unavailable periods
        String vehicleIdStr = String.valueOf(vehicleId);
        List<VehicleAvailability> currentUnavailable = vehicleAvailabilityRepository.findUnavailablePeriods(vehicleIdStr);

        // If there are any unavailable periods, consider vehicle as not generally available
        return currentUnavailable.isEmpty();
    }

    public void debugVehicleAvailability(Long vehicleId, LocalDate startDate, LocalDate endDate) {
        String vehicleIdStr = String.valueOf(vehicleId);

        System.out.println("=== DEBUG VEHICLE AVAILABILITY ===");
        System.out.println("Vehicle ID: " + vehicleId);
        System.out.println("Requested Dates: " + startDate + " to " + endDate);

        // Check bookings
        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(vehicleId, startDate, endDate);
        System.out.println("Overlapping bookings: " + overlappingBookings.size());
        overlappingBookings.forEach(booking ->
                System.out.println("  Booking: " + booking.getStartDate() + " to " + booking.getEndDate() + " Status: " + booking.getStatus()));

        // Check availability records
        List<VehicleAvailability> allAvailabilities = vehicleAvailabilityRepository.findByVehicleIdOrderByStartDateDesc(vehicleIdStr);
        System.out.println("All availability records: " + allAvailabilities.size());
        allAvailabilities.forEach(avail ->
                System.out.println("  Availability: " + avail.getStartDate() + " to " + avail.getEndDate() +
                        " Available: " + avail.getAvailable() + " Reason: " + avail.getReason()));

        List<VehicleAvailability> overlappingAvailabilities = vehicleAvailabilityRepository.findOverlappingAvailabilities(vehicleIdStr, startDate, endDate);
        System.out.println("Overlapping availability records: " + overlappingAvailabilities.size());

        // Check general availability
        System.out.println("Is vehicle generally available: " + isVehicleGenerallyAvailable(vehicleId));

        // Final availability check
        boolean finalResult = isVehicleAvailableForDates(vehicleIdStr, startDate, endDate);
        System.out.println("Final availability result: " + finalResult);
        System.out.println("=== END DEBUG ===");
    }
}