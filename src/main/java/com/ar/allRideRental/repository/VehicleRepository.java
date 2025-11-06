package com.ar.allRideRental.repository;

import com.ar.allRideRental.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
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

    // If you have a status field
    long countByStatus(String status);

    // If you have an underMaintenance field
    long countByUnderMaintenance(Boolean underMaintenance);
}