package com.cyberplatform.backend.service;

import com.cyberplatform.backend.dto.*;
import com.cyberplatform.backend.entity.User;
import com.cyberplatform.backend.entity.enums.Role;
import com.cyberplatform.backend.repository.DemandeRepository;
import com.cyberplatform.backend.repository.TacheRepository;
import com.cyberplatform.backend.repository.UserRepository;
import com.cyberplatform.backend.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final DemandeRepository demandeRepository;
    private final TacheRepository tacheRepository;

    // ─── REGISTER (CLIENT only) ───────────────────────────────────────────────
    public AuthResponse register(RegisterRequest request) {
        if (!request.getMotDePasse().equals(request.getConfirmMotDePasse())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match");
        }

        User user = User.builder()
                .raisonSociale(request.getRaisonSociale())
                .siret(request.getSiret())
                .adresseSiege(request.getAdresseSiege())
                .telephoneEntreprise(request.getTelephoneEntreprise())
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .nomContact(request.getNomContact())
                .prenomContact(request.getPrenomContact())
                .email(request.getEmail())
                .telephone(request.getTelephone())
                .code(request.getCode())
                .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                .role(Role.CLIENT)
                .build();

        try {
            User saved = userRepository.save(user);
            String token = jwtTokenProvider.generateToken(saved.getCode(), saved.getRole().name());
            return buildAuthResponse(saved, token);
        } catch (DataIntegrityViolationException e) {
            String message = e.getMostSpecificCause().getMessage();
            if (message.contains("user_email_key")) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
            } else if (message.contains("user_code_key")) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "User code already in use");
            } else {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Duplicate data detected");
            }
        }
    }

    // ─── LOGIN (all roles) ────────────────────────────────────────────────────
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByCode(request.getCode())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!passwordEncoder.matches(request.getMotDePasse(), user.getMotDePasse())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid password");
        }

        String token = jwtTokenProvider.generateToken(user.getCode(), user.getRole().name());
        return buildAuthResponse(user, token);
    }

    // ─── GET ALL USERS (paginated) ────────────────────────────────────────────
    public PagedResponse<UserDTO> getAllUsers(int page, int size) {
        PageRequest pageable = PageRequest.of(
            page, size, Sort.by(Sort.Direction.DESC, "createdAt")
        );
        Page<User> result = userRepository.findAll(pageable);
        return toPagedResponse(result);
    }

    // ─── GET USERS BY ROLE (paginated) ────────────────────────────────────────
    public PagedResponse<UserDTO> getUsersByRole(Role role, int page, int size) {
        PageRequest pageable = PageRequest.of(
            page, size, Sort.by(Sort.Direction.DESC, "createdAt")
        );
        Page<User> result = userRepository.findAllByRole(role, pageable);
        return toPagedResponse(result);
    }

    // Helper — converts a Page<User> to PagedResponse<UserDTO>
    private PagedResponse<UserDTO> toPagedResponse(Page<User> result) {
        List<UserDTO> content = result.getContent()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return PagedResponse.<UserDTO>builder()
                .content(content)
                .currentPage(result.getNumber())
                .totalPages(result.getTotalPages())
                .totalElements(result.getTotalElements())
                .size(result.getSize())
                .first(result.isFirst())
                .last(result.isLast())
                .build();
    }

    // ─── CREATE EMPLOYEE ──────────────────────────────────────────────────────
    public UserDTO createEmployee(CreateEmployeeRequest request) {
        User employee = User.builder()
                .code(request.getCode())
                .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                .email(request.getEmail())
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .telephone(request.getTelephone())
                .specialite(request.getSpecialite())
                .role(Role.EMPLOYEE)
                .build();

        try {
            return toDTO(userRepository.save(employee));
        } catch (DataIntegrityViolationException e) {
            String message = e.getMostSpecificCause().getMessage();
            if (message.contains("user_email_key")) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
            } else if (message.contains("user_code_key")) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "User code already in use");
            } else {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Duplicate data detected");
            }
        }
    }

    // ─── DELETE USER ──────────────────────────────────────────────────────────
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Safety: never delete the last admin
        if (user.getRole() == Role.ADMIN) {
            long adminCount = userRepository.countByRole(Role.ADMIN);
            if (adminCount <= 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete the last admin account");
            }
        }

        userRepository.deleteById(id);
    }

    // ─── EDIT EMPLOYEE ────────────────────────────────────────────────────────
    public UserDTO editEmployee(Long id, EditEmployeeRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Only employees can be edited via this endpoint
        if (user.getRole() != Role.EMPLOYEE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only employee accounts can be edited");
        }

        // Update fields if provided
        if (request.getNom() != null && !request.getNom().isBlank()) {
            user.setNom(request.getNom().trim());
        }
        if (request.getPrenom() != null && !request.getPrenom().isBlank()) {
            user.setPrenom(request.getPrenom().trim());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            user.setEmail(request.getEmail().trim());
        }
        if (request.getTelephone() != null) {
            user.setTelephone(request.getTelephone().trim());
        }
        if (request.getSpecialite() != null && !request.getSpecialite().isBlank()) {
            user.setSpecialite(request.getSpecialite().trim());
        }
        // Password is optional — only update if provided
        if (request.getMotDePasse() != null && !request.getMotDePasse().isBlank()) {
            user.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        }

        try {
            return toDTO(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            String message = e.getMostSpecificCause().getMessage();
            if (message.contains("user_email_key")) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
            } else {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Duplicate data detected");
            }
        }
    }

    // ─── GET USER STATS (before deletion warning) ─────────────────────────────
    public UserStatsDTO getUserStats(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.getRole() == Role.CLIENT) {
            long total  = demandeRepository.countByClientId(id);
            long active = demandeRepository.countActiveByClientId(id);
            return UserStatsDTO.builder()
                    .userId(id)
                    .role("CLIENT")
                    .totalRequests(total)
                    .activeRequests(active)
                    .totalTasks(0)
                    .activeTasks(0)
                    .build();
        }

        if (user.getRole() == Role.EMPLOYEE) {
            long total  = tacheRepository.countByEmployeeId(id);
            long active = tacheRepository.countActiveByEmployeeId(id);
            return UserStatsDTO.builder()
                    .userId(id)
                    .role("EMPLOYEE")
                    .totalRequests(0)
                    .activeRequests(0)
                    .totalTasks(total)
                    .activeTasks(active)
                    .build();
        }

        // ADMIN — no stats needed
        return UserStatsDTO.builder()
                .userId(id).role("ADMIN")
                .totalRequests(0).activeRequests(0)
                .totalTasks(0).activeTasks(0)
                .build();
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────
    private AuthResponse buildAuthResponse(User user, String token) {
        return AuthResponse.builder()
                .token(token)
                .code(user.getCode())
                .email(user.getEmail())
                .role(user.getRole())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .raisonSociale(user.getRaisonSociale())
                .build();
    }

    public UserDTO toDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .code(user.getCode())
                .email(user.getEmail())
                .role(user.getRole())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .telephone(user.getTelephone())
                .specialite(user.getSpecialite())
                .raisonSociale(user.getRaisonSociale())
                .siret(user.getSiret())
                .adresseSiege(user.getAdresseSiege())
                .telephoneEntreprise(user.getTelephoneEntreprise())
                .nomContact(user.getNomContact())
                .prenomContact(user.getPrenomContact())
                .createdAt(user.getCreatedAt())
                .build();
    }
}