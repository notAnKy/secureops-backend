package com.cyberplatform.backend.repository;

import com.cyberplatform.backend.entity.Demande;
import com.cyberplatform.backend.entity.enums.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DemandeRepository extends JpaRepository<Demande, Long> {

    // ─── Client queries ───────────────────────────────────────────────────────
    List<Demande> findByClientId(Long clientId);
    long countByClientId(Long clientId);
    long countByClientIdAndEtat(Long clientId, RequestStatus etat);
    List<Demande> findByClientIdOrderByDateSoumissionDesc(Long clientId, Pageable pageable);

    // ─── Admin queries ────────────────────────────────────────────────────────
    long countByEtat(RequestStatus etat);

    // Non-paginated — kept for dashboard stats
    List<Demande> findAllByOrderByDateSoumissionDesc();
    List<Demande> findByEtatOrderByDateSoumissionDesc(RequestStatus etat);

    // Paginated — used by ViewRequests page
    Page<Demande> findAll(Pageable pageable);
    Page<Demande> findByEtat(RequestStatus etat, Pageable pageable);

    // Count active requests (PENDING or IN_PROGRESS) for a client
    @Query("SELECT COUNT(d) FROM Demande d WHERE d.client.id = :clientId AND d.etat IN ('PENDING', 'IN_PROGRESS')")
    long countActiveByClientId(@Param("clientId") Long clientId);
}