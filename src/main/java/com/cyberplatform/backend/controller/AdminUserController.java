package com.cyberplatform.backend.controller;

import com.cyberplatform.backend.dto.CreateEmployeeRequest;
import com.cyberplatform.backend.dto.EditEmployeeRequest;
import com.cyberplatform.backend.dto.PagedResponse;
import com.cyberplatform.backend.dto.UserDTO;
import com.cyberplatform.backend.dto.UserStatsDTO;
import com.cyberplatform.backend.entity.enums.Role;
import com.cyberplatform.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    // GET /api/admin/users?page=0&size=15
    // GET /api/admin/users?role=EMPLOYEE&page=0&size=15
    @GetMapping
    public ResponseEntity<PagedResponse<UserDTO>> getUsers(
            @RequestParam(required = false) Role role,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "15") int size) {

        if (role != null) {
            return ResponseEntity.ok(userService.getUsersByRole(role, page, size));
        }
        return ResponseEntity.ok(userService.getAllUsers(page, size));
    }

    // POST /api/admin/users/employee
    @PostMapping("/employee")
    public ResponseEntity<UserDTO> createEmployee(
            @RequestBody CreateEmployeeRequest request) {
        return ResponseEntity.ok(userService.createEmployee(request));
    }

    // PUT /api/admin/users/{id}/edit
    @PutMapping("/{id}/edit")
    public ResponseEntity<UserDTO> editEmployee(
            @PathVariable Long id,
            @RequestBody EditEmployeeRequest request) {
        return ResponseEntity.ok(userService.editEmployee(id, request));
    }

    // GET /api/admin/users/{id}/stats
    @GetMapping("/{id}/stats")
    public ResponseEntity<UserStatsDTO> getUserStats(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserStats(id));
    }

    // DELETE /api/admin/users/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully.");
    }
}