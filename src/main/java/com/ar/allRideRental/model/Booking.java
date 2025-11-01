package com.ar.allRideRental.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_id", nullable = false)
    private Long vehicleId;

    // Add @JsonIgnore here to fix the serialization issue
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", insertable = false, updatable = false)
    @JsonIgnore
    private Vehicle vehicle;

    @Column(name = "vehicle_name", nullable = false)
    private String vehicleName;

    @Column(name = "customer_phone", nullable = false)
    private String customerPhone;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "customer_email")
    private String customerEmail;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "pickup_time")
    private LocalTime pickupTime;

    @Column(name = "dropoff_time")
    private LocalTime dropoffTime;

    @Column(name = "pickup_location")
    private String pickupLocation;

    @Column(name = "additional_driver")
    private boolean additionalDriver = false;

    @Column(name = "insurance_type")
    private String insurance = "basic";

    @Column(name = "total_amount", nullable = false)
    private double totalAmount;

    @Column(name = "status", nullable = false)
    private String status = "confirmed";

    @Column(name = "booking_date", nullable = false)
    private LocalDateTime bookingDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        bookingDate = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Add a transient field for vehicle image if needed
    @Transient
    private String vehicleImageUrl;

    public String getVehicleImageUrl() {
        // This will be populated manually in your service
        return vehicleImageUrl;
    }
}