package com.ar.allRideRental.service;

import com.ar.allRideRental.model.Vehicle;
import com.ar.allRideRental.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    // ===== BASIC QUERIES (backward compatible) =====
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
        // Set default sale price if not provided and vehicle is for sale/both
        if (vehicle.getSalePrice() == null && ("sale".equals(vehicle.getPurpose()) || "both".equals(vehicle.getPurpose()))) {
            vehicle.setSalePrice("₹0");
        }
        return vehicleRepository.save(vehicle);
    }

    public Vehicle updateVehicle(Long id, Vehicle vehicleDetails) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + id));

        vehicle.setName(vehicleDetails.getName());
        vehicle.setType(vehicleDetails.getType());
        vehicle.setRentPrice(vehicleDetails.getRentPrice());
        vehicle.setSalePrice(vehicleDetails.getSalePrice());
        vehicle.setFeatures(vehicleDetails.getFeatures());
        vehicle.setRating(vehicleDetails.getRating());
        vehicle.setCity(vehicleDetails.getCity());
        vehicle.setImageUrl(vehicleDetails.getImageUrl());
        vehicle.setDescription(vehicleDetails.getDescription());
        vehicle.setAvailable(vehicleDetails.getAvailable());
        vehicle.setCapacity(vehicleDetails.getCapacity());
        vehicle.setFuelType(vehicleDetails.getFuelType());
        vehicle.setTransmission(vehicleDetails.getTransmission());
        vehicle.setPurpose(vehicleDetails.getPurpose());

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
                .orElse("/images/default-vehicle.jpg");
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
        return vehicleRepository.countByStatus("maintenance");
    }

    // ===== RENT-SPECIFIC QUERIES (including dual-purpose) =====
    public List<Vehicle> getVehiclesForRent() {
        return vehicleRepository.findAvailableForRent();
    }

    public List<Vehicle> getVehiclesForRentByCity(String city) {
        return vehicleRepository.findByCityAndAvailableForRent(city);
    }

    public List<Vehicle> getVehiclesForRentByType(String type) {
        return vehicleRepository.findByTypeAndAvailableForRent(type);
    }

    public List<Vehicle> getVehiclesForRentByCityAndType(String city, String type) {
        return vehicleRepository.findByCityAndTypeAndAvailableForRent(city, type);
    }

    public List<String> getAvailableCitiesForRent() {
        return vehicleRepository.findDistinctCitiesForRent();
    }

    public long getVehicleCountForRent() {
        return vehicleRepository.countAvailableForRent();
    }

    public long getVehicleCountForRentByType(String type) {
        return vehicleRepository.countByTypeAndAvailableForRent(type);
    }

    public long getVehicleCountForRentByCity(String city) {
        return vehicleRepository.countByCityAndAvailableForRent(city);
    }

    public long getVehicleCountForRentByCityAndType(String city, String type) {
        return vehicleRepository.countByCityAndTypeAndAvailableForRent(city, type);
    }

    // ===== SALE-SPECIFIC QUERIES (including dual-purpose) =====
    public List<Vehicle> getVehiclesForSale() {
        return vehicleRepository.findAvailableForSale();
    }

    public List<Vehicle> getVehiclesForSaleByCity(String city) {
        return vehicleRepository.findByCityAndAvailableForSale(city);
    }

    public List<Vehicle> getVehiclesForSaleByType(String type) {
        return vehicleRepository.findByTypeAndAvailableForSale(type);
    }

    public List<Vehicle> getVehiclesForSaleByCityAndType(String city, String type) {
        return vehicleRepository.findByCityAndTypeAndAvailableForSale(city, type);
    }

    public List<String> getAvailableCitiesForSale() {
        return vehicleRepository.findDistinctCitiesForSale();
    }

    public long getVehicleCountForSale() {
        return vehicleRepository.countAvailableForSale();
    }

    public long getVehicleCountForSaleByType(String type) {
        return vehicleRepository.countByTypeAndAvailableForSale(type);
    }

    public long getVehicleCountForSaleByCity(String city) {
        return vehicleRepository.countByCityAndAvailableForSale(city);
    }

    public long getVehicleCountForSaleByCityAndType(String city, String type) {
        return vehicleRepository.countByCityAndTypeAndAvailableForSale(city, type);
    }

    // ===== ADMIN PURPOSE MANAGEMENT =====
    public List<Vehicle> getVehiclesByPurpose(String purpose) {
        return vehicleRepository.findByPurpose(purpose);
    }

    public long getVehicleCountByPurpose(String purpose) {
        return vehicleRepository.countByPurpose(purpose);
    }

    public List<Vehicle> getDualPurposeVehicles() {
        return vehicleRepository.findDualPurposeVehicles();
    }

    public long getDualPurposeVehiclesCount() {
        return vehicleRepository.countDualPurposeVehicles();
    }

    public boolean isVehicleAvailableForPurpose(Long vehicleId, String purpose) {
        return vehicleRepository.isVehicleAvailableForPurpose(vehicleId, purpose);
    }

    // ===== COMPREHENSIVE STATISTICS =====
    public java.util.Map<String, Object> getComprehensiveVehicleStats() {
        long totalRent = getVehicleCountForRent();
        long totalSale = getVehicleCountForSale();
        long totalDual = getDualPurposeVehiclesCount();
        long totalAvailable = getAvailableVehiclesCount();

        return java.util.Map.of(
                "totalVehicles", getTotalVehiclesCount(),
                "availableVehicles", totalAvailable,
                "rentVehicles", totalRent,
                "saleVehicles", totalSale,
                "dualPurposeVehicles", totalDual,
                "maintenanceVehicles", getMaintenanceVehiclesCount(),
                "rentOnlyVehicles", getVehicleCountByPurpose("rent"),
                "saleOnlyVehicles", getVehicleCountByPurpose("sale")
        );
    }

    // Method to get price for specific purpose
    public String getVehiclePriceForPurpose(Long vehicleId, String purpose) {
        Optional<Vehicle> vehicle = vehicleRepository.findById(vehicleId);
        if (vehicle.isPresent()) {
            return vehicle.get().getPriceForPurpose(purpose);
        }
        return "Price on request";
    }

    // VehicleService.java

    // Get distinct purpose options from existing vehicles
    public List<Map<String, String>> getPurposeOptionsFromVehicles() {
        // Get all distinct purposes from vehicles
        List<String> distinctPurposes = vehicleRepository.findDistinctPurposes();

        // Map purposes to the required format
        return distinctPurposes.stream()
                .map(this::mapPurposeToOption)
                .collect(Collectors.toList());
    }

    private Map<String, String> mapPurposeToOption(String purpose) {
        Map<String, String> option = new HashMap<>();
        option.put("value", purpose);

        // Map purpose to display label and description
        switch (purpose) {
            case "rent":
                option.put("label", "Rent Only");
                option.put("description", "Available only for rental");
                break;
            case "sale":
                option.put("label", "Sale Only");
                option.put("description", "Available only for sale");
                break;
            case "both":
                option.put("label", "Dual Purpose");
                option.put("description", "Available for both rent and sale");
                break;
            default:
                option.put("label", purpose); // Fallback to purpose value
                option.put("description", "Available for " + purpose);
        }

        return option;
    }

    // Ensure we always have the three main purposes even if no vehicles exist
    public List<Map<String, String>> getAvailablePurposeOptions() {
        List<Map<String, String>> options = getPurposeOptionsFromVehicles();

        // Ensure we always have the three main purposes
        Set<String> existingPurposes = options.stream()
                .map(opt -> opt.get("value"))
                .collect(Collectors.toSet());

        // Add missing default purposes
        if (!existingPurposes.contains("rent")) {
            options.add(Map.of(
                    "value", "rent",
                    "label", "Rent Only",
                    "description", "Available only for rental"
            ));
        }

        if (!existingPurposes.contains("sale")) {
            options.add(Map.of(
                    "value", "sale",
                    "label", "Sale Only",
                    "description", "Available only for sale"
            ));
        }

        if (!existingPurposes.contains("both")) {
            options.add(Map.of(
                    "value", "both",
                    "label", "Dual Purpose",
                    "description", "Available for both rent and sale"
            ));
        }

        // Sort by a logical order
        return options.stream()
                .sorted((a, b) -> {
                    int orderA = getPurposeOrder(a.get("value"));
                    int orderB = getPurposeOrder(b.get("value"));
                    return Integer.compare(orderA, orderB);
                })
                .collect(Collectors.toList());
    }

    private int getPurposeOrder(String purpose) {
        switch (purpose) {
            case "rent": return 1;
            case "sale": return 2;
            case "both": return 3;
            default: return 4;
        }
    }
}