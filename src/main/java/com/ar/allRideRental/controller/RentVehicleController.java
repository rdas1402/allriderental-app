package com.ar.allRideRental.controller;

import com.ar.allRideRental.model.Vehicle;
import com.ar.allRideRental.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rent/vehicles")
public class RentVehicleController {
    
    @Autowired
    private VehicleService vehicleService;
    
    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehiclesForRent() {
        List<Vehicle> vehicles = vehicleService.getVehiclesForRent();
        return ResponseEntity.ok(vehicles);
    }
    
    @GetMapping("/city/{city}")
    public ResponseEntity<List<Vehicle>> getVehiclesForRentByCity(@PathVariable String city) {
        List<Vehicle> vehicles = vehicleService.getVehiclesForRentByCity(city);
        return ResponseEntity.ok(vehicles);
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Vehicle>> getVehiclesForRentByType(@PathVariable String type) {
        List<Vehicle> vehicles = vehicleService.getVehiclesForRentByType(type);
        return ResponseEntity.ok(vehicles);
    }
    
    @GetMapping("/filter")
    public ResponseEntity<List<Vehicle>> getVehiclesForRentByCityAndType(
            @RequestParam String city, 
            @RequestParam String type) {
        List<Vehicle> vehicles = vehicleService.getVehiclesForRentByCityAndType(city, type);
        return ResponseEntity.ok(vehicles);
    }
    
    @GetMapping("/cities")
    public ResponseEntity<List<String>> getAvailableCitiesForRent() {
        List<String> cities = vehicleService.getAvailableCitiesForRent();
        return ResponseEntity.ok(cities);
    }
    
    @GetMapping("/counts")
    public ResponseEntity<Map<String, Object>> getRentVehicleCounts() {
        long totalCars = vehicleService.getVehicleCountForRentByType("Car");
        long totalBikes = vehicleService.getVehicleCountForRentByType("Bike");
        long totalVehicles = vehicleService.getVehicleCountForRent();
        
        return ResponseEntity.ok(Map.of(
            "totalVehicles", totalVehicles,
            "cars", totalCars,
            "bikes", totalBikes,
            "cities", vehicleService.getAvailableCitiesForRent().size()
        ));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleForRentById(@PathVariable Long id) {
        // Check if vehicle is available for rent
        if (!vehicleService.isVehicleAvailableForPurpose(id, "rent")) {
            return ResponseEntity.notFound().build();
        }
        
        return vehicleService.getVehicleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}