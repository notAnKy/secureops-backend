package com.cyberplatform.backend.dto;

import com.cyberplatform.backend.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private String code;
    private String email;
    private Role role;
    private String nom;
    private String prenom;
    private String raisonSociale; // for CLIENT display
    private String specialite;
    
}