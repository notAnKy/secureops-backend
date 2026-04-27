package com.cyberplatform.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "rapport")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rapport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rapport")
    private Long idRapport;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String contenu;

    @Column(name = "date_soumission", updatable = false)
    private LocalDateTime dateSoumission;

    @Column(name = "est_valide")
    private Boolean estValide;

    @ManyToOne
    @JoinColumn(name = "employe_id", nullable = false)
    private User employe;

    @ManyToOne
    @JoinColumn(name = "tache_id")
    private Tache tache;

    @ManyToOne
    @JoinColumn(name = "demande_id")
    private Demande demande;

    @PrePersist
    public void prePersist() {
        this.dateSoumission = LocalDateTime.now();
        if (this.estValide == null) {
            this.estValide = false;
        }
    }
}