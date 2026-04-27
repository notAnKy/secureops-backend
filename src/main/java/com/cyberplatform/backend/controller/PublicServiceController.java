package com.cyberplatform.backend.controller;

import com.cyberplatform.backend.dto.ServiceDTO;
import com.cyberplatform.backend.service.AdminServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class PublicServiceController {

    private final AdminServiceService adminServiceService;

    // GET /api/services
    // Available to all authenticated users (clients, employees, admins)
    // Used by clients when creating a request to browse the catalog
    @GetMapping
    public ResponseEntity<List<ServiceDTO>> getAllServices() {
        return ResponseEntity.ok(adminServiceService.getAllServices());
    }
}