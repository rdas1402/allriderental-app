package com.ar.allRideRental.repository;

import com.ar.allRideRental.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByIsAvailableTrue();
    List<Vehicle> findByCityAndIsAvailableTrue(String city);
    List<Vehicle> findByTypeAndIsAvailableTrue(String type);
    List<Vehicle> findByCityAndTypeAndIsAvailableTrue(String city, String type);
    
    @Query("SELECT DISTINCT v.city FROM Vehicle v WHERE v.isAvailable = true")
    List<String> findDistinctCities();
    
    long countByTypeAndIsAvailableTrue(String type);
    long countByCityAndIsAvailableTrue(String city);
    long countByCityAndTypeAndIsAvailableTrue(String city, String type);
}