package com.ar.allRideRental.service;

import com.ar.allRideRental.model.VehicleAvailability;
import com.ar.allRideRental.repository.VehicleAvailabilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleAvailabilityService {

    @Autowired
    private VehicleAvailabilityRepository vehicleAvailabilityRepository;

    public VehicleAvailability saveAvailability(VehicleAvailability availability) {
        // Set timestamps
        if (availability.getCreatedAt() == null) {
            availability.setCreatedAt(LocalDateTime.now());
        }
        availability.setUpdatedAt(LocalDateTime.now());

        return vehicleAvailabilityRepository.save(availability);
    }

    public List<VehicleAvailability> getAvailabilityByVehicleId(String vehicleId) {
        return vehicleAvailabilityRepository.findByVehicleIdOrderByStartDateDesc(vehicleId);
    }

    public boolean isVehicleAvailable(String vehicleId, LocalDate startDate, LocalDate endDate) {
        List<VehicleAvailability> conflicts = vehicleAvailabilityRepository
                .findConflictingUnavailablePeriods(vehicleId, startDate, endDate);
        return conflicts.isEmpty();
    }

    public void deleteAvailability(Long availabilityId) {
        vehicleAvailabilityRepository.deleteById(availabilityId);
    }

    public Optional<VehicleAvailability> findById(Long id) {
        return vehicleAvailabilityRepository.findById(id);
    }

    // Set vehicle as unavailable for specific dates
    public VehicleAvailability setVehicleUnavailable(String vehicleId, LocalDate startDate,
                                                     LocalDate endDate, String reason) {
        // Check for existing availability records in the same period
        List<VehicleAvailability> existingRecords = vehicleAvailabilityRepository
                .findOverlappingAvailabilities(vehicleId, startDate, endDate);

        // For simplicity, we'll create a new record. In production, you might want to merge overlapping periods
        VehicleAvailability availability = new VehicleAvailability();
        availability.setVehicleId(vehicleId);
        availability.setStartDate(startDate);
        availability.setEndDate(endDate);
        availability.setAvailable(false);
        availability.setReason(reason);

        return saveAvailability(availability);
    }

    // Set vehicle as available for specific dates (override any unavailable periods)
    public VehicleAvailability setVehicleAvailable(String vehicleId, LocalDate startDate,
                                                   LocalDate endDate, String reason) {
        VehicleAvailability availability = new VehicleAvailability();
        availability.setVehicleId(vehicleId);
        availability.setStartDate(startDate);
        availability.setEndDate(endDate);
        availability.setAvailable(true);
        availability.setReason(reason);

        return saveAvailability(availability);
    }

    // Remove unavailable period
    public void removeUnavailablePeriod(Long availabilityId) {
        Optional<VehicleAvailability> availability = findById(availabilityId);
        if (availability.isPresent() && !availability.get().getAvailable()) {
            deleteAvailability(availabilityId);
        }
    }

    // Get all unavailable dates for a vehicle (combining both bookings and manual unavailability)
    public List<LocalDate> getUnavailableDates(String vehicleId, LocalDate startDate, LocalDate endDate) {
        // Use the correct method name
        List<VehicleAvailability> unavailablePeriods = vehicleAvailabilityRepository
                .findUnavailablePeriodsForDateRange(vehicleId, startDate, endDate);

        List<LocalDate> unavailableDates = new ArrayList<>();
        for (VehicleAvailability period : unavailablePeriods) {
            LocalDate currentDate = period.getStartDate();
            while (!currentDate.isAfter(period.getEndDate())) {
                if (!currentDate.isBefore(startDate) && !currentDate.isAfter(endDate)) {
                    unavailableDates.add(currentDate);
                }
                currentDate = currentDate.plusDays(1);
            }
        }
        return unavailableDates;
    }
}