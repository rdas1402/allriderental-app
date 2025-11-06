package com.ar.allRideRental.repository;

import com.ar.allRideRental.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    // Basic availability queries
    List<Vehicle> findByAvailableTrue();
    List<Vehicle> findByCityAndAvailableTrue(String city);
    List<Vehicle> findByTypeAndAvailableTrue(String type);
    List<Vehicle> findByCityAndTypeAndAvailableTrue(String city, String type);

    @Query("SELECT DISTINCT v.city FROM Vehicle v WHERE v.available = true")
    List<String> findDistinctCities();

    long countByTypeAndAvailableTrue(String type);
    long countByCityAndAvailableTrue(String city);
    long countByCityAndTypeAndAvailableTrue(String city, String type);
    Optional<Vehicle> findById(Long id);

    long countByAvailable(Boolean available);
    long countByStatus(String status);
    long countByUnderMaintenance(Boolean underMaintenance);

    // ===== PURPOSE-BASED QUERIES =====

    // For RENT only
    List<Vehicle> findByPurposeAndAvailableTrue(String purpose);
    List<Vehicle> findByCityAndPurposeAndAvailableTrue(String city, String purpose);
    List<Vehicle> findByTypeAndPurposeAndAvailableTrue(String type, String purpose);
    List<Vehicle> findByCityAndTypeAndPurposeAndAvailableTrue(String city, String type, String purpose);

    // For SALE only
    @Query("SELECT DISTINCT v.city FROM Vehicle v WHERE v.available = true AND v.purpose = :purpose")
    List<String> findDistinctCitiesByPurpose(@Param("purpose") String purpose);

    long countByPurposeAndAvailableTrue(String purpose);
    long countByCityAndPurposeAndAvailableTrue(String city, String purpose);
    long countByCityAndTypeAndPurposeAndAvailableTrue(String city, String type, String purpose);

    // ===== DUAL PURPOSE QUERIES (for vehicles available for both rent and sale) =====

    // Get vehicles available for rent (including dual-purpose)
    @Query("SELECT v FROM Vehicle v WHERE v.available = true AND (v.purpose = 'rent' OR v.purpose = 'both')")
    List<Vehicle> findAvailableForRent();

    @Query("SELECT v FROM Vehicle v WHERE v.available = true AND v.city = :city AND (v.purpose = 'rent' OR v.purpose = 'both')")
    List<Vehicle> findByCityAndAvailableForRent(@Param("city") String city);

    @Query("SELECT v FROM Vehicle v WHERE v.available = true AND v.type = :type AND (v.purpose = 'rent' OR v.purpose = 'both')")
    List<Vehicle> findByTypeAndAvailableForRent(@Param("type") String type);

    @Query("SELECT v FROM Vehicle v WHERE v.available = true AND v.city = :city AND v.type = :type AND (v.purpose = 'rent' OR v.purpose = 'both')")
    List<Vehicle> findByCityAndTypeAndAvailableForRent(@Param("city") String city, @Param("type") String type);

    // Get vehicles available for sale (including dual-purpose)
    @Query("SELECT v FROM Vehicle v WHERE v.available = true AND (v.purpose = 'sale' OR v.purpose = 'both')")
    List<Vehicle> findAvailableForSale();

    @Query("SELECT v FROM Vehicle v WHERE v.available = true AND v.city = :city AND (v.purpose = 'sale' OR v.purpose = 'both')")
    List<Vehicle> findByCityAndAvailableForSale(@Param("city") String city);

    @Query("SELECT v FROM Vehicle v WHERE v.available = true AND v.type = :type AND (v.purpose = 'sale' OR v.purpose = 'both')")
    List<Vehicle> findByTypeAndAvailableForSale(@Param("type") String type);

    @Query("SELECT v FROM Vehicle v WHERE v.available = true AND v.city = :city AND v.type = :type AND (v.purpose = 'sale' OR v.purpose = 'both')")
    List<Vehicle> findByCityAndTypeAndAvailableForSale(@Param("city") String city, @Param("type") String type);

    // Get cities for rent (including dual-purpose vehicles)
    @Query("SELECT DISTINCT v.city FROM Vehicle v WHERE v.available = true AND (v.purpose = 'rent' OR v.purpose = 'both')")
    List<String> findDistinctCitiesForRent();

    // Get cities for sale (including dual-purpose vehicles)
    @Query("SELECT DISTINCT v.city FROM Vehicle v WHERE v.available = true AND (v.purpose = 'sale' OR v.purpose = 'both')")
    List<String> findDistinctCitiesForSale();

    // Count queries for rent (including dual-purpose)
    @Query("SELECT COUNT(v) FROM Vehicle v WHERE v.available = true AND (v.purpose = 'rent' OR v.purpose = 'both')")
    long countAvailableForRent();

    @Query("SELECT COUNT(v) FROM Vehicle v WHERE v.available = true AND v.type = :type AND (v.purpose = 'rent' OR v.purpose = 'both')")
    long countByTypeAndAvailableForRent(@Param("type") String type);

    @Query("SELECT COUNT(v) FROM Vehicle v WHERE v.available = true AND v.city = :city AND (v.purpose = 'rent' OR v.purpose = 'both')")
    long countByCityAndAvailableForRent(@Param("city") String city);

    @Query("SELECT COUNT(v) FROM Vehicle v WHERE v.available = true AND v.city = :city AND v.type = :type AND (v.purpose = 'rent' OR v.purpose = 'both')")
    long countByCityAndTypeAndAvailableForRent(@Param("city") String city, @Param("type") String type);

    // Count queries for sale (including dual-purpose)
    @Query("SELECT COUNT(v) FROM Vehicle v WHERE v.available = true AND (v.purpose = 'sale' OR v.purpose = 'both')")
    long countAvailableForSale();

    @Query("SELECT COUNT(v) FROM Vehicle v WHERE v.available = true AND v.type = :type AND (v.purpose = 'sale' OR v.purpose = 'both')")
    long countByTypeAndAvailableForSale(@Param("type") String type);

    @Query("SELECT COUNT(v) FROM Vehicle v WHERE v.available = true AND v.city = :city AND (v.purpose = 'sale' OR v.purpose = 'both')")
    long countByCityAndAvailableForSale(@Param("city") String city);

    @Query("SELECT COUNT(v) FROM Vehicle v WHERE v.available = true AND v.city = :city AND v.type = :type AND (v.purpose = 'sale' OR v.purpose = 'both')")
    long countByCityAndTypeAndAvailableForSale(@Param("city") String city, @Param("type") String type);

    // ===== ADMIN QUERIES FOR SPECIFIC PURPOSE =====

    // Get vehicles by specific purpose only (excluding dual-purpose)
    @Query("SELECT v FROM Vehicle v WHERE v.purpose = :purpose")
    List<Vehicle> findByPurpose(@Param("purpose") String purpose);

    @Query("SELECT COUNT(v) FROM Vehicle v WHERE v.purpose = :purpose")
    long countByPurpose(@Param("purpose") String purpose);

    // Find dual-purpose vehicles specifically
    @Query("SELECT v FROM Vehicle v WHERE v.purpose = 'both'")
    List<Vehicle> findDualPurposeVehicles();

    @Query("SELECT COUNT(v) FROM Vehicle v WHERE v.purpose = 'both'")
    long countDualPurposeVehicles();

    // Check if a vehicle is available for a specific purpose
    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END FROM Vehicle v WHERE v.id = :vehicleId AND v.available = true AND (v.purpose = :purpose OR v.purpose = 'both')")
    boolean isVehicleAvailableForPurpose(@Param("vehicleId") Long vehicleId, @Param("purpose") String purpose);

    // Find distinct purposes from existing vehicles
    @Query("SELECT DISTINCT v.purpose FROM Vehicle v WHERE v.purpose IS NOT NULL")
    List<String> findDistinctPurposes();
}