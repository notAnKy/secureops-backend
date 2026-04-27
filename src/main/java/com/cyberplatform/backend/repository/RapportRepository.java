package com.cyberplatform.backend.repository;

import com.cyberplatform.backend.entity.Rapport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RapportRepository extends JpaRepository<Rapport, Long> {

    // All reports for a specific task
    List<Rapport> findByTacheIdTache(Long taskId);

    // All reports for a specific request
    List<Rapport> findByDemandeIdDemande(Long demandeId);

    // All reports submitted by a specific employee
    List<Rapport> findByEmployeId(Long employeeId);
}