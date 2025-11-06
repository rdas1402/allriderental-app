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
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    // ===== ADMIN ENDPOINTS (manage all vehicles) =====
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

    // ===== PURPOSE MANAGEMENT ENDPOINTS =====
    @GetMapping("/purpose/{purpose}")
    public ResponseEntity<List<Vehicle>> getVehiclesByPurpose(@PathVariable String purpose) {
        if (!List.of("rent", "sale", "both").contains(purpose)) {
            return ResponseEntity.badRequest().build();
        }
        List<Vehicle> vehicles = vehicleService.getVehiclesByPurpose(purpose);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/purpose/{purpose}/count")
    public ResponseEntity<Map<String, Object>> getVehicleCountByPurpose(@PathVariable String purpose) {
        if (!List.of("rent", "sale", "both").contains(purpose)) {
            return ResponseEntity.badRequest().build();
        }
        long count = vehicleService.getVehicleCountByPurpose(purpose);
        return ResponseEntity.ok(Map.of("purpose", purpose, "count", count));
    }

    @GetMapping("/dual-purpose")
    public ResponseEntity<List<Vehicle>> getDualPurposeVehicles() {
        List<Vehicle> vehicles = vehicleService.getDualPurposeVehicles();
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/stats/comprehensive")
    public ResponseEntity<Map<String, Object>> getComprehensiveStats() {
        Map<String, Object> stats = vehicleService.getComprehensiveVehicleStats();
        return ResponseEntity.ok(stats);
    }

    // Check if vehicle is available for specific purpose
    @GetMapping("/{id}/available-for/{purpose}")
    public ResponseEntity<Map<String, Object>> isVehicleAvailableForPurpose(
            @PathVariable Long id,
            @PathVariable String purpose) {
        if (!List.of("rent", "sale").contains(purpose)) {
            return ResponseEntity.badRequest().build();
        }

        boolean isAvailable = vehicleService.isVehicleAvailableForPurpose(id, purpose);
        return ResponseEntity.ok(Map.of(
                "vehicleId", id,
                "purpose", purpose,
                "available", isAvailable
        ));
    }
    // Admin to set price for vehicles
    @PutMapping("/{id}/prices")
    public ResponseEntity<Vehicle> updateVehiclePrices(
            @PathVariable Long id,
            @RequestBody Map<String, String> priceData) {
        try {
            Vehicle vehicle = vehicleService.getVehicleById(id)
                    .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + id));

            if (priceData.containsKey("rentPrice")) {
                vehicle.setRentPrice(priceData.get("rentPrice"));
            }
            if (priceData.containsKey("salePrice")) {
                vehicle.setSalePrice(priceData.get("salePrice"));
            }

            Vehicle updatedVehicle = vehicleService.updateVehicle(id, vehicle);
            return ResponseEntity.ok(updatedVehicle);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}