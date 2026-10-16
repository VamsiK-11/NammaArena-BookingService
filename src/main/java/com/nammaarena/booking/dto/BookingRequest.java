package com.nammaarena.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BookingRequest {
    @NotNull(message = "Turf ID is required")
    private Long turfId;
    
    @NotBlank(message = "Sport is required")
    private String sport;
    
    @NotNull(message = "Booking date is required")
    @FutureOrPresent(message = "Booking date cannot be in the past")
    private LocalDate bookingDate;
    
    @NotBlank(message = "Start time is required")
    private String startTime;
    
    @NotBlank(message = "End time is required")
    private String endTime;
    
    private Integer duration; // Will be calculated automatically
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;
}