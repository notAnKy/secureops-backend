package com.cyberplatform.backend.repository;

import com.cyberplatform.backend.entity.Tache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TacheRepository extends JpaRepository<Tache, Long> {

    // All tasks for a specific request
    List<Tache> findByDemandeIdDemande(Long demandeId);

    // All tasks assigned to a specific employee
    @Query("SELECT t FROM Tache t JOIN t.employes e WHERE e.id = :employeeId")
    List<Tache> findAllByEmployeeId(@Param("employeeId") Long employeeId);

    // Count total tasks assigned to an employee
    @Query("SELECT COUNT(t) FROM Tache t JOIN t.employes e WHERE e.id = :employeeId")
    long countByEmployeeId(@Param("employeeId") Long employeeId);

    // Count active tasks (NOT_STARTED or IN_PROGRESS) assigned to an employee
    @Query("SELECT COUNT(t) FROM Tache t JOIN t.employes e WHERE e.id = :employeeId AND t.statut IN ('NOT_STARTED', 'IN_PROGRESS')")
    long countActiveByEmployeeId(@Param("employeeId") Long employeeId);
}