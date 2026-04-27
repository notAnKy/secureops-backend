package com.cyberplatform.backend.controller;

import com.cyberplatform.backend.dto.DashboardStatsDTO;
import com.cyberplatform.backend.entity.enums.RequestStatus;
import com.cyberplatform.backend.entity.enums.Role;
import com.cyberplatform.backend.repository.DemandeRepository;
import com.cyberplatform.backend.repository.ServiceRepository;
import com.cyberplatform.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class DashboardController {

    private final UserRepository userRepository;
    private final DemandeRepository demandeRepository;
    private final ServiceRepository serviceRepository;

    // GET /api/admin/dashboard/stats
    // Returns all counts the admin dashboard needs in one call
    @GetMapping("/dashboard/stats")
    public ResponseEntity<DashboardStatsDTO> getStats() {

        DashboardStatsDTO stats = DashboardStatsDTO.builder()
                // Users
                .totalUsers(userRepository.count())
                .totalClients(userRepository.countByRole(Role.CLIENT))
                .totalEmployees(userRepository.countByRole(Role.EMPLOYEE))
                // Requests
                .totalRequests(demandeRepository.count())
                .pendingRequests(demandeRepository.countByEtat(RequestStatus.PENDING))
                .inProgressRequests(demandeRepository.countByEtat(RequestStatus.IN_PROGRESS))
                .completedRequests(demandeRepository.countByEtat(RequestStatus.COMPLETED))
                .cancelledRequests(demandeRepository.countByEtat(RequestStatus.CANCELLED))
                // Services
                .totalServices(serviceRepository.count())
                .build();

        return ResponseEntity.ok(stats);
    }
}