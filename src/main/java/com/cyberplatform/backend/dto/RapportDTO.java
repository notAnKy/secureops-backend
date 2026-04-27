package com.cyberplatform.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RapportDTO {
    private Long idRapport;
    private String contenu;
    private LocalDateTime dateSoumission;
    private Boolean estValide;
    private Long employeId;
    private String employeCode;
    private String employeNom;
    private String employePrenom;
    private Long tacheId;
    private Long demandeId;
}