package com.ar.allRideRental.controller;

import com.ar.allRideRental.model.Vehicle;
import com.ar.allRideRental.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sale/vehicles")
public class SaleVehicleController {
    
    @Autowired
    private VehicleService vehicleService;
    
    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehiclesForSale() {
        List<Vehicle> vehicles = vehicleService.getVehiclesForSale();
        return ResponseEntity.ok(vehicles);
    }
    
    @GetMapping("/city/{city}")
    public ResponseEntity<List<Vehicle>> getVehiclesForSaleByCity(@PathVariable String city) {
        List<Vehicle> vehicles = vehicleService.getVehiclesForSaleByCity(city);
        return ResponseEntity.ok(vehicles);
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Vehicle>> getVehiclesForSaleByType(@PathVariable String type) {
        List<Vehicle> vehicles = vehicleService.getVehiclesForSaleByType(type);
        return ResponseEntity.ok(vehicles);
    }
    
    @GetMapping("/filter")
    public ResponseEntity<List<Vehicle>> getVehiclesForSaleByCityAndType(
            @RequestParam String city, 
            @RequestParam String type) {
        List<Vehicle> vehicles = vehicleService.getVehiclesForSaleByCityAndType(city, type);
        return ResponseEntity.ok(vehicles);
    }
    
    @GetMapping("/cities")
    public ResponseEntity<List<String>> getAvailableCitiesForSale() {
        List<String> cities = vehicleService.getAvailableCitiesForSale();
        return ResponseEntity.ok(cities);
    }
    
    @GetMapping("/counts")
    public ResponseEntity<Map<String, Object>> getSaleVehicleCounts() {
        long totalCars = vehicleService.getVehicleCountForSaleByType("Car");
        long totalBikes = vehicleService.getVehicleCountForSaleByType("Bike");
        long totalVehicles = vehicleService.getVehicleCountForSale();
        
        return ResponseEntity.ok(Map.of(
            "totalVehicles", totalVehicles,
            "cars", totalCars,
            "bikes", totalBikes,
            "cities", vehicleService.getAvailableCitiesForSale().size()
        ));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleForSaleById(@PathVariable Long id) {
        // Check if vehicle is available for sale
        if (!vehicleService.isVehicleAvailableForPurpose(id, "sale")) {
            return ResponseEntity.notFound().build();
        }
        
        return vehicleService.getVehicleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}