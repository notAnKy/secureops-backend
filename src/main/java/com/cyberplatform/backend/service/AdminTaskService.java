package com.cyberplatform.backend.service;

import com.cyberplatform.backend.dto.AssignEmployeeRequest;
import com.cyberplatform.backend.dto.CreateTacheRequest;
import com.cyberplatform.backend.dto.TacheDTO;
import com.cyberplatform.backend.dto.UserDTO;
import com.cyberplatform.backend.entity.Demande;
import com.cyberplatform.backend.entity.Tache;
import com.cyberplatform.backend.entity.User;
import com.cyberplatform.backend.entity.enums.Role;
import com.cyberplatform.backend.entity.enums.TaskStatus;
import com.cyberplatform.backend.repository.DemandeRepository;
import com.cyberplatform.backend.repository.TacheRepository;
import com.cyberplatform.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminTaskService {

    private final TacheRepository tacheRepository;
    private final DemandeRepository demandeRepository;
    private final UserRepository userRepository;

    // ─── Get all tasks for a request ─────────────────────────────────────────
    public List<TacheDTO> getTasksByRequest(Long demandeId) {
        // Verify request exists
        demandeRepository.findById(demandeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found"));

        return tacheRepository.findByDemandeIdDemande(demandeId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ─── Create task on a request ─────────────────────────────────────────────
    public TacheDTO createTask(Long demandeId, CreateTacheRequest request) {
        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task description is required");
        }

        Demande demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found"));

        Tache tache = Tache.builder()
                .description(request.getDescription())
                .statut(TaskStatus.NOT_STARTED)
                .dateDebut(request.getDateDebut())
                .dateFinPrevue(request.getDateFinPrevue())
                .demande(demande)
                .employes(new ArrayList<>())
                .build();

        return toDTO(tacheRepository.save(tache));
    }

    // ─── Delete a task ────────────────────────────────────────────────────────
    public void deleteTask(Long taskId) {
        Tache tache = tacheRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        tacheRepository.delete(tache);
    }

    // ─── Assign employee to task ──────────────────────────────────────────────
    public TacheDTO assignEmployee(Long taskId, AssignEmployeeRequest request) {
        Tache tache = tacheRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        User employee = userRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));

        if (employee.getRole() != Role.EMPLOYEE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not an employee");
        }

        // Check not already assigned
        boolean alreadyAssigned = tache.getEmployes().stream()
                .anyMatch(e -> e.getId().equals(employee.getId()));
        if (alreadyAssigned) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Employee already assigned to this task");
        }

        tache.getEmployes().add(employee);
        return toDTO(tacheRepository.save(tache));
    }

    // ─── Unassign employee from task ──────────────────────────────────────────
    public TacheDTO unassignEmployee(Long taskId, Long employeeId) {
        Tache tache = tacheRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        tache.getEmployes().removeIf(e -> e.getId().equals(employeeId));
        return toDTO(tacheRepository.save(tache));
    }

    // ─── Get all employees (for assignment picker) ────────────────────────────
    public List<UserDTO> getAllEmployees() {
        return userRepository.findAllByRole(Role.EMPLOYEE)
                .stream()
                .map(u -> UserDTO.builder()
                        .id(u.getId())
                        .code(u.getCode())
                        .nom(u.getNom())
                        .prenom(u.getPrenom())
                        .email(u.getEmail())
                        .specialite(u.getSpecialite())
                        .role(u.getRole())
                        .build())
                .collect(Collectors.toList());
    }

    // ─── toDTO helper ─────────────────────────────────────────────────────────
    public TacheDTO toDTO(Tache t) {
        List<TacheDTO.EmployeeDTO> employeeDTOs = new ArrayList<>();
        if (t.getEmployes() != null) {
            employeeDTOs = t.getEmployes().stream()
                    .map(e -> TacheDTO.EmployeeDTO.builder()
                            .id(e.getId())
                            .code(e.getCode())
                            .nom(e.getNom())
                            .prenom(e.getPrenom())
                            .specialite(e.getSpecialite())
                            .email(e.getEmail())
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
}