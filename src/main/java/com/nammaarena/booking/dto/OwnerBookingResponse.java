package com.nammaarena.booking.dto;

import com.nammaarena.booking.enums.BookingStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class OwnerBookingResponse {
    private Long id;
    private Long userId;
    private String userName;
    private Long turfId;
    private String turfName;
    private String sport;
    private LocalDate bookingDate;
    private String startTime;
    private String endTime;
    private Integer duration;
    private BigDecimal price;
    private BookingStatus status;
    private String paymentId;
    private LocalDateTime createdAt;
}