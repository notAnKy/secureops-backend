// package com.cyberplatform.backend.controller;

// import com.cyberplatform.backend.entity.User;
// import com.cyberplatform.backend.entity.enums.Role;
// import com.cyberplatform.backend.repository.UserRepository;
// import lombok.Data;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.web.bind.annotation.*;

// @RestController
// @RequestMapping("/api/setup")
// @RequiredArgsConstructor
// public class SetupController {

//     private final UserRepository userRepository;
//     private final PasswordEncoder passwordEncoder;

//     // POST http://localhost:8081/api/setup/create-admin
//     // Body: { "code": "ADMIN001", "password": "Admin@2024", "email": "admin@secureops.com" }
//     @PostMapping("/create-admin")
//     public ResponseEntity<String> createAdmin(@RequestBody CreateAdminRequest request) {

//         if (userRepository.findByCode(request.getCode()).isPresent()) {
//             return ResponseEntity.badRequest().body("User with this code already exists.");
//         }

//         User admin = User.builder()
//                 .code(request.getCode())
//                 .motDePasse(passwordEncoder.encode(request.getPassword()))
//                 .email(request.getEmail())
//                 .role(Role.ADMIN)
//                 .nom("Admin")
//                 .prenom("Super")
//                 .build();

//         userRepository.save(admin);
//         return ResponseEntity.ok("Admin created successfully. DELETE THIS ENDPOINT NOW.");
//     }

//     @Data
//     public static class CreateAdminRequest {
//         private String code;
//         private String password;
//         private String email;
//     }
// }