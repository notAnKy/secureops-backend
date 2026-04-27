package com.cyberplatform.backend.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateTacheRequest {
    private String description;
    private LocalDate dateDebut;
    private LocalDate dateFinPrevue;
}