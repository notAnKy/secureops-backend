package com.cyberplatform.backend.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreateDemandeRequest {
    private String description;
    private String priorite;           // HIGH, MEDIUM, LOW
    private LocalDate dateLimite;      // optional deadline
    private List<Long> serviceIds;     // services the client selected
}