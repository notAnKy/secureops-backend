package com.cyberplatform.backend.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String code;        // login by code (not email)
    private String motDePasse;
}