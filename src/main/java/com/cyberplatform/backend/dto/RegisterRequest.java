package com.cyberplatform.backend.dto;

import lombok.Data;

@Data
public class RegisterRequest {

    // Step 1: Company
    private String raisonSociale;
    private String siret;   
    private String adresseSiege;
    private String telephoneEntreprise;

    // Step 2: Contact
    private String nom;           // First name of the contact person
    private String prenom;        // Last name of the contact person
    private String nomContact;    // Company contact last name (if different)
    private String prenomContact; // Company contact first name (if different)
    private String email;
    private String telephone;

    // Step 3: Account
    private String code;
    private String motDePasse;
    private String confirmMotDePasse;

    // No role here because all sign-ups will automatically be CLIENT
}