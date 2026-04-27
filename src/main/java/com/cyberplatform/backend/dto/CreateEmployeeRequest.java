package com.cyberplatform.backend.dto;

import lombok.Data;

@Data
public class CreateEmployeeRequest {
    private String code;
    private String motDePasse;
    private String email;
    private String nom;
    private String prenom;
    private String telephone;
    private String specialite;
}