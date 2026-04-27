package com.cyberplatform.backend.entity;

import com.cyberplatform.backend.entity.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "tache")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tache")
    private Long idTache;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private TaskStatus statut;

    @Column(name = "date_debut")
    private LocalDate dateDebut;

    @Column(name = "date_fin_prevue")
    private LocalDate dateFinPrevue;

    @Column(name = "date_fin_reelle")
    private LocalDate dateFinReelle;

    // 🔹 Many Taches → One Demande
    @ManyToOne
    @JoinColumn(name = "demande_id", nullable = false)
    private Demande demande;

    // 🔹 Many-to-Many with User (Employés)
    @ManyToMany
    @JoinTable(
        name = "tache_employe",
        joinColumns = @JoinColumn(name = "tache_id"),
        inverseJoinColumns = @JoinColumn(name = "employe_id")
    )
    private List<User> employes;
}