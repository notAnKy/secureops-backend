package com.cyberplatform.backend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServiceRequest {
    private String nom;
    private String description;
    private String type;
    private BigDecimal prix;
}