package com.ar.allRideRental.model;

import jakarta.persistence.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "vehicles")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String type; // "Car" or "Bike"
    
    @Column(nullable = false)
    private String price;
    
    @ElementCollection
    @CollectionTable(name = "vehicle_features", joinColumns = @JoinColumn(name = "vehicle_id"))
    @Column(name = "feature")
    private List<String> features;
    
    @Column(nullable = false)
    private Double rating;
    
    @Column(nullable = false)
    private String city;
    
    @Column(nullable = false)
    private String imageUrl;
    
    private String description;
    
    @Column(nullable = false)
    private boolean isAvailable = true;
    
    private Integer capacity;
    private String fuelType;
    private String transmission;
    
    // Constructors
    public Vehicle() {}
    
    public Vehicle(String name, String type, String price, List<String> features, 
                   Double rating, String city, String imageUrl) {
        this.name = name;
        this.type = type;
        this.price = price;
        this.features = features;
        this.rating = rating;
        this.city = city;
        this.imageUrl = imageUrl;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }
    
    public List<String> getFeatures() { return features; }
    public void setFeatures(List<String> features) { this.features = features; }
    
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    @JsonProperty("isAvailable")
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
    
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    
    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }
    
    public String getTransmission() { return transmission; }
    public void setTransmission(String transmission) { this.transmission = transmission; }
}