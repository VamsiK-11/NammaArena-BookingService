package com.nammaarena.booking.dto;

import lombok.Data;

@Data
public class TurfDto {
    private Long id;
    private String name;
    private String location;
    private Long ownerId;
    private String status;
}