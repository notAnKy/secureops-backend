package com.cyberplatform.backend.controller;

import com.cyberplatform.backend.dto.*;
import com.cyberplatform.backend.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    // GET /api/employee/profile
    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getProfile(Authentication auth) {
        return ResponseEntity.ok(employeeService.getProfile(auth.getName()));
    }

    // PUT /api/employee/profile
    @PutMapping("/profile")
    public ResponseEntity<UserDTO> updateProfile(
            Authentication auth,
            @RequestBody EmployeeProfileRequest request) {
        return ResponseEntity.ok(employeeService.updateProfile(auth.getName(), request));
    }

    // GET /api/employee/tasks
    @GetMapping("/tasks")
    public ResponseEntity<List<TacheDTO>> getMyTasks(Authentication auth) {
        return ResponseEntity.ok(employeeService.getMyTasks(auth.getName()));
    }

    // GET /api/employee/tasks/{id}
    @GetMapping("/tasks/{id}")
    public ResponseEntity<TacheDTO> getTaskById(Authentication auth, @PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getTaskById(auth.getName(), id));
    }

    // PUT /api/employee/tasks/{id}/status
    // Body: { "statut": "IN_PROGRESS" }
    @PutMapping("/tasks/{id}/status")
    public ResponseEntity<TacheDTO> updateStatus(
            Authentication auth,
            @PathVariable Long id,
            @RequestBody UpdateTaskStatusRequest request) {
        return ResponseEntity.ok(employeeService.updateTaskStatus(auth.getName(), id, request));
    }

    // POST /api/employee/tasks/{id}/report
    // Body: { "contenu": "..." }
    @PostMapping("/tasks/{id}/report")
    public ResponseEntity<RapportDTO> submitReport(
            Authentication auth,
            @PathVariable Long id,
            @RequestBody SubmitRapportRequest request) {
        return ResponseEntity.ok(employeeService.submitReport(auth.getName(), id, request));
    }

    // GET /api/employee/tasks/{id}/reports
    // Returns all reports submitted for this task
    @GetMapping("/tasks/{id}/reports")
    public ResponseEntity<List<RapportDTO>> getTaskReports(
            Authentication auth,
            @PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getTaskReports(auth.getName(), id));
    }
}