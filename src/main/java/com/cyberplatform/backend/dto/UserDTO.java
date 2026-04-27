package com.cyberplatform.backend.dto;

import com.cyberplatform.backend.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String code;
    private String email;
    private Role role;
    private String nom;
    private String prenom;
    private String telephone;
    private String specialite;       // employee
    private String raisonSociale;    // client
    private String siret;            // client
    private String adresseSiege;     // client
    private String telephoneEntreprise; // client
    private String nomContact;       // client
    private String prenomContact;    // client
    private LocalDateTime createdAt;
}