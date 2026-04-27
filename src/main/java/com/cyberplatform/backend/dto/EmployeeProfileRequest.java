package com.cyberplatform.backend.dto;

import lombok.Data;

@Data
public class EmployeeProfileRequest {
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String currentPassword;
    private String newPassword;
}