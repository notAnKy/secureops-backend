package com.cyberplatform.backend.service;

import com.cyberplatform.backend.entity.User;
import com.cyberplatform.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    // ─── Step 1: User requests password reset ────────────────────────────────
    // Generates a token, saves it, sends email
    public void requestReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElse(null);

        // IMPORTANT: Always return success even if email not found
        // This prevents attackers from knowing which emails are registered
        if (user == null) {
            log.info("Password reset requested for unknown email: {}", email);
            return;
        }

        // Generate a secure random token
        String token = UUID.randomUUID().toString().replace("-", "");

        // Save token + expiry (1 hour from now) on the user
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        // Send the reset email
        String name = user.getRaisonSociale() != null
                ? user.getRaisonSociale()
                : (user.getPrenom() + " " + user.getNom()).trim();

        emailService.sendPasswordResetEmail(email, name, token);
        log.info("Password reset email sent to {}", email);
    }

    // ─── Step 2: User submits new password with token ─────────────────────────
    public void resetPassword(String token, String newPassword) {
        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token");
        }

        if (newPassword == null || newPassword.length() < 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be at least 8 characters");
        }

        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Invalid or expired reset link"));

        // Check token has not expired
        if (user.getResetTokenExpiry() == null ||
                LocalDateTime.now().isAfter(user.getResetTokenExpiry())) {
            // Clean up expired token
            user.setResetToken(null);
            user.setResetTokenExpiry(null);
            userRepository.save(user);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Reset link has expired. Please request a new one.");
        }

        // Update password + clear token
        user.setMotDePasse(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);

        log.info("Password reset successful for user: {}", user.getEmail());
    }
}