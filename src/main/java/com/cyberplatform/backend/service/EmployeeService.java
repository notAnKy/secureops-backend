package com.cyberplatform.backend.service;

import com.cyberplatform.backend.dto.*;
import com.cyberplatform.backend.entity.Rapport;
import com.cyberplatform.backend.entity.Tache;
import com.cyberplatform.backend.entity.User;
import com.cyberplatform.backend.repository.RapportRepository;
import com.cyberplatform.backend.repository.TacheRepository;
import com.cyberplatform.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final UserRepository userRepository;
    private final TacheRepository tacheRepository;
    private final RapportRepository rapportRepository;
    private final PasswordEncoder passwordEncoder;

    // ─── Get all tasks assigned to this employee ──────────────────────────────
    public List<TacheDTO> getMyTasks(String code) {
        User employee = findEmployeeByCode(code);
        return tacheRepository.findAllByEmployeeId(employee.getId())
                .stream().map(this::toTacheDTO).collect(Collectors.toList());
    }

    // ─── Get one task (only if assigned to this employee) ────────────────────
    public TacheDTO getTaskById(String code, Long taskId) {
        User employee = findEmployeeByCode(code);
        Tache tache = findTaskOrThrow(taskId);
        verifyAssigned(tache, employee);
        return toTacheDTO(tache);
    }

    // ─── Update task status ───────────────────────────────────────────────────
    public TacheDTO updateTaskStatus(String code, Long taskId, UpdateTaskStatusRequest request) {
        User employee = findEmployeeByCode(code);
        Tache tache = findTaskOrThrow(taskId);
        verifyAssigned(tache, employee);

        if (request.getStatut() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required");
        }

        tache.setStatut(request.getStatut());
        return toTacheDTO(tacheRepository.save(tache));
    }

    // ─── Submit report for a task ─────────────────────────────────────────────
    public RapportDTO submitReport(String code, Long taskId, SubmitRapportRequest request) {
        User employee = findEmployeeByCode(code);
        Tache tache = findTaskOrThrow(taskId);
        verifyAssigned(tache, employee);

        if (request.getContenu() == null || request.getContenu().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Report content is required");
        }

        Rapport rapport = Rapport.builder()
                .contenu(request.getContenu())
                .employe(employee)
                .tache(tache)
                .demande(tache.getDemande())
                .build();
        // dateSoumission and estValide set by @PrePersist

        return toRapportDTO(rapportRepository.save(rapport));
    }

    // ─── Get reports for a task ───────────────────────────────────────────────
    public List<RapportDTO> getTaskReports(String code, Long taskId) {
        User employee = findEmployeeByCode(code);
        Tache tache = findTaskOrThrow(taskId);
        verifyAssigned(tache, employee);

        return rapportRepository.findByTacheIdTache(taskId)
                .stream().map(this::toRapportDTO).collect(Collectors.toList());
    }

    // ─── Get employee profile ─────────────────────────────────────────────────
    public UserDTO getProfile(String code) {
        User employee = findEmployeeByCode(code);
        return toUserDTO(employee);
    }

    // ─── Update employee profile ──────────────────────────────────────────────
    public UserDTO updateProfile(String code, EmployeeProfileRequest request) {
        User employee = findEmployeeByCode(code);

        if (request.getNom() != null && !request.getNom().isBlank())
            employee.setNom(request.getNom().trim());

        if (request.getPrenom() != null && !request.getPrenom().isBlank())
            employee.setPrenom(request.getPrenom().trim());

        if (request.getEmail() != null && !request.getEmail().isBlank())
            employee.setEmail(request.getEmail().trim());

        if (request.getTelephone() != null)
            employee.setTelephone(request.getTelephone().trim());

        if (request.getCurrentPassword() != null && !request.getCurrentPassword().isBlank()
                && request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
            if (!passwordEncoder.matches(request.getCurrentPassword(), employee.getMotDePasse())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
            }
            employee.setMotDePasse(passwordEncoder.encode(request.getNewPassword()));
        }

        try {
            return toUserDTO(userRepository.save(employee));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────
    private User findEmployeeByCode(String code) {
        return userRepository.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
    }

    private Tache findTaskOrThrow(Long taskId) {
        return tacheRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
    }

    private void verifyAssigned(Tache tache, User employee) {
        boolean isAssigned = tache.getEmployes().stream()
                .anyMatch(e -> e.getId().equals(employee.getId()));
        if (!isAssigned) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
    }

    public TacheDTO toTacheDTO(Tache t) {
        List<TacheDTO.EmployeeDTO> employeeDTOs = new ArrayList<>();
        if (t.getEmployes() != null) {
            employeeDTOs = t.getEmployes().stream()
                    .map(e -> TacheDTO.EmployeeDTO.builder()
                            .id(e.getId()).code(e.getCode())
                            .nom(e.getNom()).prenom(e.getPrenom())
                            .specialite(e.getSpecialite()).email(e.getEmail())
                            .build())
                    .collect(Collectors.toList());
        }
        return TacheDTO.builder()
                .idTache(t.getIdTache())
                .description(t.getDescription())
                .statut(t.getStatut() != null ? t.getStatut().name() : null)
                .dateDebut(t.getDateDebut())
                .dateFinPrevue(t.getDateFinPrevue())
                .dateFinReelle(t.getDateFinReelle())
                .demandeId(t.getDemande().getIdDemande())
                .employes(employeeDTOs)
                .build();
    }

    public RapportDTO toRapportDTO(Rapport r) {
        return RapportDTO.builder()
                .idRapport(r.getIdRapport())
                .contenu(r.getContenu())
                .dateSoumission(r.getDateSoumission())
                .estValide(r.getEstValide())
                .employeId(r.getEmploye().getId())
                .employeCode(r.getEmploye().getCode())
                .employeNom(r.getEmploye().getNom())
                .employePrenom(r.getEmploye().getPrenom())
                .tacheId(r.getTache() != null ? r.getTache().getIdTache() : null)
                .demandeId(r.getDemande() != null ? r.getDemande().getIdDemande() : null)
                .build();
    }

    private UserDTO toUserDTO(User u) {
        return UserDTO.builder()
                .id(u.getId())
                .code(u.getCode())
                .email(u.getEmail())
                .role(u.getRole())
                .nom(u.getNom())
                .prenom(u.getPrenom())
                .telephone(u.getTelephone())
                .specialite(u.getSpecialite())
                .createdAt(u.getCreatedAt())
                .build();
    }
}