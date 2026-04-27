package com.cyberplatform.backend.dto;

import lombok.Data;

@Data
public class ClientProfileRequest {

    // Contact info — editable
    private String nom;
    private String prenom;
    private String email;
    private String telephone;

    // Company info — editable
    private String adresseSiege;
    private String telephoneEntreprise;

    // Password change — optional, only updated if provided
    private String currentPassword;
    private String newPassword;
}