package com.cyberplatform.backend.controller;

import com.cyberplatform.backend.dto.ClientDashboardStatsDTO;
import com.cyberplatform.backend.dto.ClientProfileRequest;
import com.cyberplatform.backend.dto.ClientRapportDTO;
import com.cyberplatform.backend.dto.CreateDemandeRequest;
import com.cyberplatform.backend.dto.DemandeDTO;
import com.cyberplatform.backend.dto.UserDTO;
import com.cyberplatform.backend.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
public class DemandeController {

    private final ClientService clientService;

    // GET /api/client/dashboard/stats
    @GetMapping("/dashboard/stats")
    public ResponseEntity<ClientDashboardStatsDTO> getDashboardStats(Authentication auth) {
        return ResponseEntity.ok(clientService.getDashboardStats(auth.getName()));
    }

    // GET /api/client/profile
    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getProfile(Authentication auth) {
        return ResponseEntity.ok(clientService.getProfile(auth.getName()));
    }

    // PUT /api/client/profile
    // Body: { nom, prenom, email, telephone, adresseSiege, telephoneEntreprise, currentPassword, newPassword }
    @PutMapping("/profile")
    public ResponseEntity<UserDTO> updateProfile(
            Authentication auth,
            @RequestBody ClientProfileRequest request) {
        return ResponseEntity.ok(clientService.updateProfile(auth.getName(), request));
    }

    // POST /api/client/requests
    @PostMapping("/requests")
    public ResponseEntity<DemandeDTO> createRequest(
            Authentication auth,
            @RequestBody CreateDemandeRequest request) {
        return ResponseEntity.ok(clientService.createRequest(auth.getName(), request));
    }

    // GET /api/client/requests
    @GetMapping("/requests")
    public ResponseEntity<List<DemandeDTO>> getMyRequests(Authentication auth) {
        return ResponseEntity.ok(clientService.getMyRequests(auth.getName()));
    }

    // GET /api/client/requests/{id}
    @GetMapping("/requests/{id}")
    public ResponseEntity<DemandeDTO> getRequestById(
            Authentication auth,
            @PathVariable Long id) {
        return ResponseEntity.ok(clientService.getRequestById(auth.getName(), id));
    }

    // PUT /api/client/requests/{id}/cancel
    @PutMapping("/requests/{id}/cancel")
    public ResponseEntity<DemandeDTO> cancelRequest(
            Authentication auth,
            @PathVariable Long id) {
        return ResponseEntity.ok(clientService.cancelRequest(auth.getName(), id));
    }

    // GET /api/client/requests/{id}/reports
    @GetMapping("/requests/{id}/reports")
    public ResponseEntity<List<ClientRapportDTO>> getValidatedReports(
            Authentication auth,
            @PathVariable Long id) {
        return ResponseEntity.ok(clientService.getValidatedReports(auth.getName(), id));
    }
}