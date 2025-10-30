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
        return vehicleRepository.findByIsAvailableTrue();
    }
    
    public List<Vehicle> getVehiclesByCity(String city) {
        return vehicleRepository.findByCityAndIsAvailableTrue(city);
    }
    
    public List<Vehicle> getVehiclesByType(String type) {
        return vehicleRepository.findByTypeAndIsAvailableTrue(type);
    }
    
    public List<Vehicle> getVehiclesByCityAndType(String city, String type) {
        return vehicleRepository.findByCityAndTypeAndIsAvailableTrue(city, type);
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
        vehicle.setAvailable(vehicleDetails.isAvailable());
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
        return vehicleRepository.countByTypeAndIsAvailableTrue(type);
    }
    
    public long getVehicleCountByCity(String city) {
        return vehicleRepository.countByCityAndIsAvailableTrue(city);
    }
    
    public long getVehicleCountByCityAndType(String city, String type) {
        return vehicleRepository.countByCityAndTypeAndIsAvailableTrue(city, type);
    }
}