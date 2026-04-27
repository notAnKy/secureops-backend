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
public class ClientRapportDTO {
    private Long idRapport;
    private String contenu;
    private LocalDateTime dateSoumission;
    // Employee info — client can see who wrote it
    private String employePrenom;
    private String employeNom;
    private String employeSpecialite;
    // Task info
    private Long tacheId;
}