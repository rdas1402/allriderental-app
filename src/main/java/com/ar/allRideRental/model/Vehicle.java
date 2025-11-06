package com.ar.allRideRental.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehicles")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type", nullable = false)
    private String type;

    // UPDATED: Separate price fields for rent and sale
    @Column(name = "rent_price")
    private String rentPrice;

    @Column(name = "sale_price")
    private String salePrice;

    @Column(name = "rating")
    private Double rating = 0.0;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "is_available")
    private Boolean available = true;

    @Column(name = "under_maintenance")
    private Boolean underMaintenance = false;

    @Column(name = "status")
    private String status = "available";

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "description")
    private String description;

    @Column(name = "fuel_type")
    private String fuelType;

    @Column(name = "transmission")
    private String transmission;

    // NEW: Purpose field - can be 'rent', 'sale', or 'both'
    @Column(name = "purpose", nullable = false)
    private String purpose = "rent";

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // One-to-Many relationship with VehicleFeature
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private List<VehicleFeature> features = new ArrayList<>();

    // Constructors
    public Vehicle() {
    }

    public Vehicle(String name, String type, String rentPrice, String city, String imageUrl) {
        this.name = name;
        this.type = type;
        this.rentPrice = rentPrice;
        this.city = city;
        this.imageUrl = imageUrl;
        this.available = true;
        this.underMaintenance = false;
        this.status = "available";
        this.rating = 0.0;
        this.purpose = "rent";
    }

    public Vehicle(String name, String type, String rentPrice, Double rating, String city, String imageUrl) {
        this.name = name;
        this.type = type;
        this.rentPrice = rentPrice;
        this.rating = rating;
        this.city = city;
        this.imageUrl = imageUrl;
        this.available = true;
        this.underMaintenance = false;
        this.status = "available";
        this.purpose = "rent";
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    // UPDATED: Price getters and setters
    public String getRentPrice() {
        return rentPrice;
    }

    public void setRentPrice(String rentPrice) {
        this.rentPrice = rentPrice;
    }

    public String getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(String salePrice) {
        this.salePrice = salePrice;
    }

    // Backward compatibility - get price based on purpose
    public String getPrice() {
        if ("sale".equals(purpose) && salePrice != null) {
            return salePrice;
        }
        return rentPrice != null ? rentPrice : "₹0/day";
    }

    public void setPrice(String price) {
        // For backward compatibility, set rent price
        this.rentPrice = price;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Boolean getUnderMaintenance() {
        return underMaintenance;
    }

    public void setUnderMaintenance(Boolean underMaintenance) {
        this.underMaintenance = underMaintenance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    // NEW: Purpose getter and setter
    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<VehicleFeature> getFeatures() {
        return features;
    }

    public void setFeatures(List<VehicleFeature> features) {
        this.features = features;
    }

    // Helper methods for features
    public void addFeature(VehicleFeature feature) {
        if (this.features == null) {
            this.features = new ArrayList<>();
        }
        this.features.add(feature);
    }

    public void addFeature(String featureName) {
        if (this.features == null) {
            this.features = new ArrayList<>();
        }
        this.features.add(new VehicleFeature(this.id, featureName));
    }

    public void removeFeature(VehicleFeature feature) {
        if (this.features != null) {
            this.features.remove(feature);
        }
    }

    // Helper methods for purpose
    public boolean isForRent() {
        return "rent".equals(purpose) || "both".equals(purpose);
    }

    public boolean isForSale() {
        return "sale".equals(purpose) || "both".equals(purpose);
    }

    public boolean isDualPurpose() {
        return "both".equals(purpose);
    }

    // Helper method to get appropriate price based on context
    public String getDisplayPrice() {
        if ("sale".equals(purpose)) {
            return salePrice != null ? salePrice : "Price on request";
        } else if ("rent".equals(purpose)) {
            return rentPrice != null ? rentPrice : "₹0/day";
        } else {
            // For dual-purpose, show both prices
            return (rentPrice != null ? rentPrice : "₹0/day") + " | " + (salePrice != null ? salePrice : "Price on request");
        }
    }

    // Helper method to get price for specific purpose
    public String getPriceForPurpose(String purpose) {
        if ("sale".equals(purpose)) {
            return salePrice != null ? salePrice : "Price on request";
        } else {
            return rentPrice != null ? rentPrice : "₹0/day";
        }
    }

    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", rentPrice='" + rentPrice + '\'' +
                ", salePrice='" + salePrice + '\'' +
                ", rating=" + rating +
                ", city='" + city + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", available=" + available +
                ", underMaintenance=" + underMaintenance +
                ", status='" + status + '\'' +
                ", purpose='" + purpose + '\'' +
                ", capacity=" + capacity +
                ", description='" + description + '\'' +
                ", fuelType='" + fuelType + '\'' +
                ", transmission='" + transmission + '\'' +
                ", features=" + (features != null ? features.size() : 0) +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}