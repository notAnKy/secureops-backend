package com.cyberplatform.backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthEntryPoint authEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())             // uses your CorsConfig bean
            .csrf(csrf -> csrf.disable())                // disable CSRF (we use JWT, not sessions)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // no sessions
            )
            .exceptionHandling(ex ->
                ex.authenticationEntryPoint(authEntryPoint) // handles 401 responses
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()  // login + register = public
                // .requestMatchers("/api/setup/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN") // only admins can access /api/admin/*
                .requestMatchers("/api/client/**").hasAnyRole("CLIENT") 
                .requestMatchers("/api/employee/**").hasAnyRole("EMPLOYEE") 
                .anyRequest().authenticated()                 // everything else needs JWT
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}