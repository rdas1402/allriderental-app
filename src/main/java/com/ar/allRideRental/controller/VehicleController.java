package com.ar.allRideRental.controller;

import com.ar.allRideRental.model.Vehicle;
import com.ar.allRideRental.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vehicles")
//@CrossOrigin(origins = "http://localhost:3000")
public class VehicleController {
    
    @Autowired
    private VehicleService vehicleService;
    
    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        List<Vehicle> vehicles = vehicleService.getAllVehicles();
        return ResponseEntity.ok(vehicles);
    }
    
    @GetMapping("/city/{city}")
    public ResponseEntity<List<Vehicle>> getVehiclesByCity(@PathVariable String city) {
        List<Vehicle> vehicles = vehicleService.getVehiclesByCity(city);
        return ResponseEntity.ok(vehicles);
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Vehicle>> getVehiclesByType(@PathVariable String type) {
        List<Vehicle> vehicles = vehicleService.getVehiclesByType(type);
        return ResponseEntity.ok(vehicles);
    }
    
    @GetMapping("/filter")
    public ResponseEntity<List<Vehicle>> getVehiclesByCityAndType(
            @RequestParam String city, 
            @RequestParam String type) {
        List<Vehicle> vehicles = vehicleService.getVehiclesByCityAndType(city, type);
        return ResponseEntity.ok(vehicles);
    }
    
    @GetMapping("/cities")
    public ResponseEntity<List<String>> getAvailableCities() {
        List<String> cities = vehicleService.getAvailableCities();
        return ResponseEntity.ok(cities);
    }
    
    @GetMapping("/counts")
    public ResponseEntity<Map<String, Object>> getVehicleCounts() {
        long totalCars = vehicleService.getVehicleCountByType("Car");
        long totalBikes = vehicleService.getVehicleCountByType("Bike");
        long totalVehicles = totalCars + totalBikes;
        
        return ResponseEntity.ok(Map.of(
            "totalVehicles", totalVehicles,
            "cars", totalCars,
            "bikes", totalBikes
        ));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id) {
        return vehicleService.getVehicleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Vehicle> createVehicle(@RequestBody Vehicle vehicle) {
        Vehicle createdVehicle = vehicleService.createVehicle(vehicle);
        return ResponseEntity.ok(createdVehicle);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Vehicle> updateVehicle(@PathVariable Long id, @RequestBody Vehicle vehicleDetails) {
        try {
            Vehicle updatedVehicle = vehicleService.updateVehicle(id, vehicleDetails);
            return ResponseEntity.ok(updatedVehicle);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        try {
            vehicleService.deleteVehicle(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}