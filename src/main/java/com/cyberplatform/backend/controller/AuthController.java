package com.cyberplatform.backend.controller;

import com.cyberplatform.backend.dto.AuthResponse;
import com.cyberplatform.backend.dto.ForgotPasswordRequest;
import com.cyberplatform.backend.dto.LoginRequest;
import com.cyberplatform.backend.dto.RegisterRequest;
import com.cyberplatform.backend.dto.ResetPasswordRequest;
import com.cyberplatform.backend.service.PasswordResetService;
import com.cyberplatform.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final PasswordResetService passwordResetService;

    // POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    // POST /api/auth/forgot-password
    // Body: { "email": "user@example.com" }
    // Always returns 200 OK — never reveals if email exists
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @RequestBody ForgotPasswordRequest request) {
        passwordResetService.requestReset(request.getEmail());
        return ResponseEntity.ok(Map.of(
            "message", "If an account with that email exists, a reset link has been sent."
        ));
    }

    // POST /api/auth/reset-password
    // Body: { "token": "abc123...", "newPassword": "newpass123" }
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(Map.of(
            "message", "Password reset successfully. You can now log in with your new password."
        ));
    }
}