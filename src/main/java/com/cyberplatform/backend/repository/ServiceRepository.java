package com.cyberplatform.backend.repository;

import com.cyberplatform.backend.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ServiceRepository extends JpaRepository<Service, Long> {

    // Count how many requests use this service
    @Query("SELECT COUNT(d) FROM Demande d JOIN d.services s WHERE s.id = :serviceId")
    long countRequestsUsingService(@Param("serviceId") Long serviceId);
}