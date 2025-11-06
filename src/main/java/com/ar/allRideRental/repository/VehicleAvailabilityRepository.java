// VehicleAvailabilityRepository.java
package com.ar.allRideRental.repository;

import com.ar.allRideRental.model.VehicleAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VehicleAvailabilityRepository extends JpaRepository<VehicleAvailability, Long> {

    List<VehicleAvailability> findByVehicleIdOrderByStartDateDesc(String vehicleId);

    // Find overlapping availability records
    @Query("SELECT va FROM VehicleAvailability va WHERE " +
            "va.vehicleId = :vehicleId AND " +
            "va.startDate <= :endDate AND va.endDate >= :startDate")
    List<VehicleAvailability> findOverlappingAvailabilities(@Param("vehicleId") String vehicleId,
                                                            @Param("startDate") LocalDate startDate,
                                                            @Param("endDate") LocalDate endDate);

    // Find only conflicting UNAVAILABLE periods
    @Query("SELECT va FROM VehicleAvailability va WHERE " +
            "va.vehicleId = :vehicleId AND " +
            "va.isAvailable = false AND " +
            "va.startDate <= :endDate AND va.endDate >= :startDate")
    List<VehicleAvailability> findConflictingUnavailablePeriods(@Param("vehicleId") String vehicleId,
                                                                @Param("startDate") LocalDate startDate,
                                                                @Param("endDate") LocalDate endDate);

    // Check if vehicle is manually unavailable for period
    @Query("SELECT COUNT(va) > 0 FROM VehicleAvailability va WHERE " +
            "va.vehicleId = :vehicleId AND " +
            "va.isAvailable = false AND " +
            "va.startDate <= :endDate AND va.endDate >= :startDate")
    boolean existsUnavailablePeriod(@Param("vehicleId") String vehicleId,
                                    @Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);

    // Check if vehicle is manually unavailable for specific date
    @Query("SELECT COUNT(va) > 0 FROM VehicleAvailability va WHERE " +
            "va.vehicleId = :vehicleId AND " +
            "va.isAvailable = false AND " +
            "va.startDate <= :date AND va.endDate >= :date")
    boolean existsUnavailableForDate(@Param("vehicleId") String vehicleId,
                                     @Param("date") LocalDate date);

    // Get all unavailable periods in range - THIS IS THE MISSING METHOD
    @Query("SELECT va FROM VehicleAvailability va WHERE " +
            "va.vehicleId = :vehicleId AND " +
            "va.isAvailable = false AND " +
            "va.startDate <= :endDate AND va.endDate >= :startDate")
    List<VehicleAvailability> findUnavailablePeriodsInRange(@Param("vehicleId") String vehicleId,
                                                            @Param("startDate") LocalDate startDate,
                                                            @Param("endDate") LocalDate endDate);

    // Find current unavailable periods
    @Query("SELECT va FROM VehicleAvailability va WHERE " +
            "va.vehicleId = :vehicleId AND " +
            "va.isAvailable = false AND " +
            "va.endDate >= :currentDate")
    List<VehicleAvailability> findCurrentUnavailablePeriods(@Param("vehicleId") String vehicleId,
                                                            @Param("currentDate") LocalDate currentDate);

    // Check if vehicle is unavailable for period (manual unavailability)
    @Query("SELECT COUNT(va) > 0 FROM VehicleAvailability va WHERE " +
            "va.vehicleId = :vehicleId AND " +
            "va.isAvailable = false AND " +
            "va.startDate <= :endDate AND va.endDate >= :startDate")
    boolean isVehicleUnavailableForPeriod(@Param("vehicleId") String vehicleId,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);

    // Find unavailable periods (for general availability check) - ALIAS for findUnavailablePeriodsInRange
    @Query("SELECT va FROM VehicleAvailability va WHERE " +
            "va.vehicleId = :vehicleId AND " +
            "va.isAvailable = false")
    List<VehicleAvailability> findUnavailablePeriods(@Param("vehicleId") String vehicleId);

    // NEW: Find unavailable dates in range - This replaces findUnavailableDatesInRange
    @Query("SELECT va FROM VehicleAvailability va WHERE " +
            "va.vehicleId = :vehicleId AND " +
            "va.isAvailable = false AND " +
            "va.startDate <= :endDate AND va.endDate >= :startDate")
    List<VehicleAvailability> findUnavailablePeriodsForDateRange(@Param("vehicleId") String vehicleId,
                                                                 @Param("startDate") LocalDate startDate,
                                                                 @Param("endDate") LocalDate endDate);
}