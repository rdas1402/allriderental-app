package com.ar.allRideRental.service;

import com.ar.allRideRental.model.Vehicle;
import com.ar.allRideRental.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findByAvailableTrue();
    }
    
    public List<Vehicle> getVehiclesByCity(String city) {
        return vehicleRepository.findByCityAndAvailableTrue(city);
    }
    
    public List<Vehicle> getVehiclesByType(String type) {
        return vehicleRepository.findByTypeAndAvailableTrue(type);
    }
    
    public List<Vehicle> getVehiclesByCityAndType(String city, String type) {
        return vehicleRepository.findByCityAndTypeAndAvailableTrue(city, type);
    }
    
    public List<String> getAvailableCities() {
        return vehicleRepository.findDistinctCities();
    }
    
    public Optional<Vehicle> getVehicleById(Long id) {
        return vehicleRepository.findById(id);
    }
    
    public Vehicle createVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }
    
    public Vehicle updateVehicle(Long id, Vehicle vehicleDetails) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + id));
        
        vehicle.setName(vehicleDetails.getName());
        vehicle.setType(vehicleDetails.getType());
        vehicle.setPrice(vehicleDetails.getPrice());
        vehicle.setFeatures(vehicleDetails.getFeatures());
        vehicle.setRating(vehicleDetails.getRating());
        vehicle.setCity(vehicleDetails.getCity());
        vehicle.setImageUrl(vehicleDetails.getImageUrl());
        vehicle.setDescription(vehicleDetails.getDescription());
        vehicle.setAvailable(vehicleDetails.getAvailable());
        vehicle.setCapacity(vehicleDetails.getCapacity());
        vehicle.setFuelType(vehicleDetails.getFuelType());
        vehicle.setTransmission(vehicleDetails.getTransmission());
        
        return vehicleRepository.save(vehicle);
    }
    
    public void deleteVehicle(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + id));
        vehicle.setAvailable(false);
        vehicleRepository.save(vehicle);
    }
    
    public long getVehicleCountByType(String type) {
        return vehicleRepository.countByTypeAndAvailableTrue(type);
    }
    
    public long getVehicleCountByCity(String city) {
        return vehicleRepository.countByCityAndAvailableTrue(city);
    }
    
    public long getVehicleCountByCityAndType(String city, String type) {
        return vehicleRepository.countByCityAndTypeAndAvailableTrue(city, type);
    }

    // Add this method to get vehicle image URL
    public String getVehicleImageUrl(Long vehicleId) {
        return vehicleRepository.findById(vehicleId)
                .map(Vehicle::getImageUrl)
                .orElse("/images/default-vehicle.jpg"); // Fallback image
    }

    public boolean vehicleExists(String vehicleId) {
        return vehicleRepository.existsById(Long.valueOf(vehicleId));
    }

    public long getTotalVehiclesCount() {
        return vehicleRepository.count();
    }

    public long getAvailableVehiclesCount() {
        return vehicleRepository.countByAvailable(true);
    }

    public long getMaintenanceVehiclesCount() {
        // Assuming you have a maintenance status field
        return vehicleRepository.countByStatus("maintenance");
    }

//    // If you don't have a status field, you can use this alternative:
//    public long getMaintenanceVehiclesCount() {
//        return vehicleRepository.countByUnderMaintenance(true);
//    }
}