package com.cyberplatform.backend.entity;

import com.cyberplatform.backend.entity.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "demande")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Demande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_demande")
    private Long idDemande;

    @Column(name = "date_soumission", updatable = false)
    private LocalDateTime dateSoumission;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private RequestStatus etat;

    private String priorite;

    @Column(name = "date_limite")
    private LocalDate dateLimite;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    @ManyToMany
    @JoinTable(
        name = "demande_service",
        joinColumns = @JoinColumn(name = "demande_id"),
        inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<Service> services;

    // ✅ Auto-set submission date and default status on create
    @PrePersist
    public void prePersist() {
        this.dateSoumission = LocalDateTime.now();
        if (this.etat == null) {
            this.etat = RequestStatus.PENDING;
        }
    }
}