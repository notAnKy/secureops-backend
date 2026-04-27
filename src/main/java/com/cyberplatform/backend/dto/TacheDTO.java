package com.cyberplatform.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TacheDTO {
    private Long idTache;
    private String description;
    private String statut;
    private LocalDate dateDebut;
    private LocalDate dateFinPrevue;
    private LocalDate dateFinReelle;
    private Long demandeId;
    private List<EmployeeDTO> employes;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EmployeeDTO {
        private Long id;
        private String code;
        private String nom;
        private String prenom;
        private String specialite;
        private String email;
    }
}