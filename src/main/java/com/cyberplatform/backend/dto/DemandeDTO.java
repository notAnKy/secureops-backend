package com.cyberplatform.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DemandeDTO {
    private Long idDemande;
    private String description;
    private String etat;
    private String priorite;
    private LocalDateTime dateSoumission;
    private LocalDate dateLimite;
    private Long clientId;
    private String clientCode;
    private String clientRaisonSociale;
    private List<ServiceDTO> services;
}