package com.cyberplatform.backend.controller;

import com.cyberplatform.backend.dto.RapportDTO;
import com.cyberplatform.backend.service.AdminReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminReportController {

    private final AdminReportService adminReportService;

    // GET /api/admin/tasks/{taskId}/reports
    @GetMapping("/tasks/{taskId}/reports")
    public ResponseEntity<List<RapportDTO>> getReportsByTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(adminReportService.getReportsByTask(taskId));
    }

    // GET /api/admin/requests/{demandeId}/reports
    @GetMapping("/requests/{demandeId}/reports")
    public ResponseEntity<List<RapportDTO>> getReportsByRequest(@PathVariable Long demandeId) {
        return ResponseEntity.ok(adminReportService.getReportsByRequest(demandeId));
    }

    // PUT /api/admin/reports/{id}/validate
    @PutMapping("/reports/{id}/validate")
    public ResponseEntity<RapportDTO> validateReport(@PathVariable Long id) {
        return ResponseEntity.ok(adminReportService.validateReport(id));
    }

    // PUT /api/admin/reports/{id}/invalidate
    @PutMapping("/reports/{id}/invalidate")
    public ResponseEntity<RapportDTO> invalidateReport(@PathVariable Long id) {
        return ResponseEntity.ok(adminReportService.invalidateReport(id));
    }
}