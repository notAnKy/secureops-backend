package com.cyberplatform.backend.controller;

import com.cyberplatform.backend.dto.DemandeDTO;
import com.cyberplatform.backend.dto.PagedResponse;
import com.cyberplatform.backend.dto.UpdateStatusRequest;
import com.cyberplatform.backend.entity.enums.RequestStatus;
import com.cyberplatform.backend.service.AdminRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/requests")
@RequiredArgsConstructor
public class AdminRequestController {

    private final AdminRequestService adminRequestService;

    // GET /api/admin/requests?page=0&size=15
    // GET /api/admin/requests?status=PENDING&page=0&size=15
    @GetMapping
    public ResponseEntity<PagedResponse<DemandeDTO>> getAllRequests(
            @RequestParam(required = false) RequestStatus status,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "15") int size) {
        return ResponseEntity.ok(adminRequestService.getAllRequests(status, page, size));
    }

    // GET /api/admin/requests/{id}
    @GetMapping("/{id}")
    public ResponseEntity<DemandeDTO> getRequest(@PathVariable Long id) {
        return ResponseEntity.ok(adminRequestService.getRequestById(id));
    }

    // PUT /api/admin/requests/{id}/status
    // Body: { "etat": "IN_PROGRESS" }
    @PutMapping("/{id}/status")
    public ResponseEntity<DemandeDTO> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(adminRequestService.updateStatus(id, request));
    }
}