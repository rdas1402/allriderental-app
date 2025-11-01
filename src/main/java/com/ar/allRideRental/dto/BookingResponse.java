package com.ar.allRideRental.dto;

import com.ar.allRideRental.model.Booking;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
public class BookingResponse {
    private Long id;
    private Long vehicleId;
    private String vehicleName;
    private String vehicleImageUrl;
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
    private LocalDateTime bookingDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BookingResponse(Booking booking, String vehicleImageUrl) {
        this.id = booking.getId();
        this.vehicleId = booking.getVehicleId();
        this.vehicleName = booking.getVehicleName();
        this.vehicleImageUrl = vehicleImageUrl;
        this.customerPhone = booking.getCustomerPhone();
        this.customerName = booking.getCustomerName();
        this.customerEmail = booking.getCustomerEmail();
        this.startDate = booking.getStartDate();
        this.endDate = booking.getEndDate();
        this.pickupTime = booking.getPickupTime();
        this.dropoffTime = booking.getDropoffTime();
        this.pickupLocation = booking.getPickupLocation();
        this.additionalDriver = booking.isAdditionalDriver();
        this.insurance = booking.getInsurance();
        this.totalAmount = booking.getTotalAmount();
        this.status = booking.getStatus();
        this.bookingDate = booking.getBookingDate();
        this.createdAt = booking.getCreatedAt();
        this.updatedAt = booking.getUpdatedAt();
    }
}