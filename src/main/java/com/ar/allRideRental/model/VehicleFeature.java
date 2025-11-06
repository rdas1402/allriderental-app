package com.ar.allRideRental.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "vehicle_features")
public class VehicleFeature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_id")
    private Long vehicleId;

    @Column(name = "feature")
    private String feature;

    // Many-to-One relationship (optional, if you want bidirectional mapping)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", insertable = false, updatable = false)
    @JsonIgnore
    private Vehicle vehicle;

    // Constructors
    public VehicleFeature() {
    }

    public VehicleFeature(Long vehicleId, String feature) {
        this.vehicleId = vehicleId;
        this.feature = feature;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    @Override
    public String toString() {
        return "VehicleFeature{" +
                "id=" + id +
                ", vehicleId=" + vehicleId +
                ", feature='" + feature + '\'' +
                '}';
    }
}