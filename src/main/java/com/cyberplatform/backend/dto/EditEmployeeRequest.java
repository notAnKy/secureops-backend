package com.cyberplatform.backend.dto;

import lombok.Data;

@Data
public class EditEmployeeRequest {
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String specialite;
    private String motDePasse; // optional — only updated if provided
}