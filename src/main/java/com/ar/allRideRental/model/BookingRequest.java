package com.ar.allRideRental.model;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    // FIX: Add JSON format annotation to handle both "9:00" and "09:00" formats
    @JsonFormat(pattern = "H:mm")
    private LocalTime pickupTime;

    @JsonFormat(pattern = "H:mm")
    private LocalTime dropoffTime;

    private String pickupLocation;
    private boolean additionalDriver = false;
    private String insurance = "basic";
    private double totalAmount;
    private String status = "confirmed";
}