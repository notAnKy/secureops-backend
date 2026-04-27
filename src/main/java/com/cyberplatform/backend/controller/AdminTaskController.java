package com.cyberplatform.backend.controller;

import com.cyberplatform.backend.dto.AssignEmployeeRequest;
import com.cyberplatform.backend.dto.CreateTacheRequest;
import com.cyberplatform.backend.dto.TacheDTO;
import com.cyberplatform.backend.dto.UserDTO;
import com.cyberplatform.backend.service.AdminTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminTaskController {

    private final AdminTaskService adminTaskService;

    // GET /api/admin/requests/{id}/tasks
    @GetMapping("/api/admin/requests/{id}/tasks")
    public ResponseEntity<List<TacheDTO>> getTasksByRequest(@PathVariable Long id) {
        return ResponseEntity.ok(adminTaskService.getTasksByRequest(id));
    }

    // POST /api/admin/requests/{id}/tasks
    // Body: { description, dateDebut, dateFinPrevue }
    @PostMapping("/api/admin/requests/{id}/tasks")
    public ResponseEntity<TacheDTO> createTask(
            @PathVariable Long id,
            @RequestBody CreateTacheRequest request) {
        return ResponseEntity.ok(adminTaskService.createTask(id, request));
    }

    // DELETE /api/admin/tasks/{taskId}
    @DeleteMapping("/api/admin/tasks/{taskId}")
    public ResponseEntity<String> deleteTask(@PathVariable Long taskId) {
        adminTaskService.deleteTask(taskId);
        return ResponseEntity.ok("Task deleted successfully.");
    }

    // POST /api/admin/tasks/{taskId}/assign
    // Body: { employeeId: 5 }
    @PostMapping("/api/admin/tasks/{taskId}/assign")
    public ResponseEntity<TacheDTO> assignEmployee(
            @PathVariable Long taskId,
            @RequestBody AssignEmployeeRequest request) {
        return ResponseEntity.ok(adminTaskService.assignEmployee(taskId, request));
    }

    // DELETE /api/admin/tasks/{taskId}/assign/{employeeId}
    @DeleteMapping("/api/admin/tasks/{taskId}/assign/{employeeId}")
    public ResponseEntity<TacheDTO> unassignEmployee(
            @PathVariable Long taskId,
            @PathVariable Long employeeId) {
        return ResponseEntity.ok(adminTaskService.unassignEmployee(taskId, employeeId));
    }

    // GET /api/admin/employees
    // Returns all employees for the assignment picker
    @GetMapping("/api/admin/employees")
    public ResponseEntity<List<UserDTO>> getAllEmployees() {
        return ResponseEntity.ok(adminTaskService.getAllEmployees());
    }
}