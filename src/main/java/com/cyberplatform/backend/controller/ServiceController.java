package com.cyberplatform.backend.controller;

import com.cyberplatform.backend.dto.ServiceDTO;
import com.cyberplatform.backend.dto.ServiceRequest;
import com.cyberplatform.backend.service.AdminServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/services")
@RequiredArgsConstructor
public class ServiceController {

    private final AdminServiceService adminServiceService;

    // GET /api/admin/services
    @GetMapping
    public ResponseEntity<List<ServiceDTO>> getAllServices() {
        return ResponseEntity.ok(adminServiceService.getAllServices());
    }

    // GET /api/admin/services/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ServiceDTO> getService(@PathVariable Long id) {
        return ResponseEntity.ok(adminServiceService.getServiceById(id));
    }

    // POST /api/admin/services
    // Body: { "nom": "...", "description": "...", "type": "...", "prix": 0.00 }
    @PostMapping
    public ResponseEntity<ServiceDTO> createService(@RequestBody ServiceRequest request) {
        return ResponseEntity.ok(adminServiceService.createService(request));
    }

    // PUT /api/admin/services/{id}
    // Body: { "nom": "...", "description": "...", "type": "...", "prix": 0.00 }
    @PutMapping("/{id}")
    public ResponseEntity<ServiceDTO> updateService(
            @PathVariable Long id,
            @RequestBody ServiceRequest request) {
        return ResponseEntity.ok(adminServiceService.updateService(id, request));
    }

    // GET /api/admin/services/{id}/usage
    // Returns how many requests use this service — called before delete warning
    @GetMapping("/{id}/usage")
    public ResponseEntity<Long> getServiceUsage(@PathVariable Long id) {
        return ResponseEntity.ok(adminServiceService.getServiceUsage(id));
    }

    // DELETE /api/admin/services/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteService(@PathVariable Long id) {
        adminServiceService.deleteService(id);
        return ResponseEntity.ok("Service deleted successfully.");
    }
}