package com.ar.allRideRental.model;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class BookingRequest {
    private Long vehicleId;
    private String vehicleName;
    private String customerPhone;
    private String customerName;
    private String customerEmail;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime pickupTime;
    private LocalTime dropoffTime;
    private String pickupLocation;
    private boolean additionalDriver;
    private String insurance;
    private double totalAmount;
    private String status;
}