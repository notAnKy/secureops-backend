package com.cyberplatform.backend.entity;

import com.cyberplatform.backend.entity.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "\"user\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(name = "mot_de_passe", nullable = false)
    private String motDePasse;

    @Column(unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String nom;
    private String prenom;
    private String telephone;

    // 👨‍💼 Employee
    private String specialite;

    // 🏢 Client
    @Column(name = "raison_sociale")
    private String raisonSociale;

    private String siret;

    @Column(name = "adresse_siege")
    private String adresseSiege;

    @Column(name = "telephone_entreprise")
    private String telephoneEntreprise;

    @Column(name = "nom_contact")
    private String nomContact;

    @Column(name = "prenom_contact")
    private String prenomContact;

    // 🕒 Created At
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // 🔑 Password reset token — generated when user requests reset
    @Column(name = "reset_token")
    private String resetToken;

    // ⏰ When the reset token expires (1 hour from generation)
    @Column(name = "reset_token_expiry")
    private LocalDateTime resetTokenExpiry;

    // 🔥 Auto set createdAt before insert
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}