package com.cyberplatform.backend.service;

import com.cyberplatform.backend.dto.*;
import com.cyberplatform.backend.entity.Demande;
import com.cyberplatform.backend.entity.Service;
import com.cyberplatform.backend.entity.User;
import com.cyberplatform.backend.entity.enums.RequestStatus;
import com.cyberplatform.backend.repository.DemandeRepository;
import com.cyberplatform.backend.repository.RapportRepository;
import com.cyberplatform.backend.repository.ServiceRepository;
import com.cyberplatform.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ClientService {

    private final UserRepository userRepository;
    private final DemandeRepository demandeRepository;
    private final ServiceRepository serviceRepository;
    private final RapportRepository rapportRepository;
    private final PasswordEncoder passwordEncoder;

    // ─── Get validated reports for a request (client view) ───────────────────
    // Only returns est_valide=true reports — client never sees pending ones
    public List<ClientRapportDTO> getValidatedReports(String code, Long requestId) {
        User client = findClientByCode(code);
        Demande demande = demandeRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found"));

        // Security: make sure the request belongs to this client
        if (!demande.getClient().getId().equals(client.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        return rapportRepository.findByDemandeIdDemande(requestId)
                .stream()
                .filter(r -> Boolean.TRUE.equals(r.getEstValide()))
                .map(r -> ClientRapportDTO.builder()
                        .idRapport(r.getIdRapport())
                        .contenu(r.getContenu())
                        .dateSoumission(r.getDateSoumission())
                        .employePrenom(r.getEmploye() != null ? r.getEmploye().getPrenom() : null)
                        .employeNom(r.getEmploye() != null ? r.getEmploye().getNom() : null)
                        .employeSpecialite(r.getEmploye() != null ? r.getEmploye().getSpecialite() : null)
                        .tacheId(r.getTache() != null ? r.getTache().getIdTache() : null)
                        .build())
                .collect(Collectors.toList());
    }

    // ─── Dashboard stats ──────────────────────────────────────────────────────
    public ClientDashboardStatsDTO getDashboardStats(String code) {
        User client = findClientByCode(code);
        Long clientId = client.getId();

        long total      = demandeRepository.countByClientId(clientId);
        long pending    = demandeRepository.countByClientIdAndEtat(clientId, RequestStatus.PENDING);
        long inProgress = demandeRepository.countByClientIdAndEtat(clientId, RequestStatus.IN_PROGRESS);
        long completed  = demandeRepository.countByClientIdAndEtat(clientId, RequestStatus.COMPLETED);
        long cancelled  = demandeRepository.countByClientIdAndEtat(clientId, RequestStatus.CANCELLED);

        List<Demande> recent = demandeRepository
                .findByClientIdOrderByDateSoumissionDesc(clientId, PageRequest.of(0, 5));

        List<ClientDashboardStatsDTO.RecentRequestDTO> recentDTOs = recent.stream()
                .map(d -> ClientDashboardStatsDTO.RecentRequestDTO.builder()
                        .id(d.getIdDemande())
                        .description(d.getDescription())
                        .etat(d.getEtat() != null ? d.getEtat().name() : null)
                        .priorite(d.getPriorite())
                        .dateSoumission(d.getDateSoumission() != null ? d.getDateSoumission().toString() : null)
                        .dateLimite(d.getDateLimite() != null ? d.getDateLimite().toString() : null)
                        .build())
                .collect(Collectors.toList());

        return ClientDashboardStatsDTO.builder()
                .totalRequests(total)
                .pendingRequests(pending)
                .inProgressRequests(inProgress)
                .completedRequests(completed)
                .cancelledRequests(cancelled)
                .recentRequests(recentDTOs)
                .build();
    }

    // ─── Create request ───────────────────────────────────────────────────────
    public DemandeDTO createRequest(String code, CreateDemandeRequest request) {
        // Validate
        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Description is required");
        }
        if (request.getServiceIds() == null || request.getServiceIds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one service must be selected");
        }

        User client = findClientByCode(code);

        // Fetch selected services
        List<Service> services = serviceRepository.findAllById(request.getServiceIds());
        if (services.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selected services not found");
        }

        // Build and save
        Demande demande = Demande.builder()
                .description(request.getDescription())
                .priorite(request.getPriorite())
                .dateLimite(request.getDateLimite())
                .client(client)
                .services(services)
                .build();
        // etat and dateSoumission are set automatically by @PrePersist

        Demande saved = demandeRepository.save(demande);
        return toDTO(saved);
    }

    // ─── Get all requests for this client ─────────────────────────────────────
    public List<DemandeDTO> getMyRequests(String code) {
        User client = findClientByCode(code);
        return demandeRepository
                .findByClientIdOrderByDateSoumissionDesc(client.getId(), PageRequest.of(0, 100))
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ─── Get one request (only if it belongs to this client) ─────────────────
    public DemandeDTO getRequestById(String code, Long requestId) {
        User client = findClientByCode(code);
        Demande demande = demandeRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found"));

        // Security: make sure it belongs to this client
        if (!demande.getClient().getId().equals(client.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        return toDTO(demande);
    }

    // ─── Cancel request (only if PENDING and belongs to this client) ──────────
    public DemandeDTO cancelRequest(String code, Long requestId) {
        User client = findClientByCode(code);
        Demande demande = demandeRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found"));

        // Security: make sure it belongs to this client
        if (!demande.getClient().getId().equals(client.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        // Only PENDING requests can be cancelled by the client
        if (demande.getEtat() != RequestStatus.PENDING) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Only pending requests can be cancelled. Contact support if you need to cancel an in-progress request."
            );
        }

        demande.setEtat(RequestStatus.CANCELLED);
        return toDTO(demandeRepository.save(demande));
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────
    private User findClientByCode(String code) {
        return userRepository.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public DemandeDTO toDTO(Demande d) {
        List<ServiceDTO> serviceDTOs = new ArrayList<>();
        if (d.getServices() != null) {
            serviceDTOs = d.getServices().stream()
                    .map(s -> ServiceDTO.builder()
                            .id(s.getId())
                            .nom(s.getNom())
                            .description(s.getDescription())
                            .type(s.getType())
                            .prix(s.getPrix())
                            .createdAt(s.getCreatedAt())
                            .build())
                    .collect(Collectors.toList());
        }

        return DemandeDTO.builder()
                .idDemande(d.getIdDemande())
                .description(d.getDescription())
                .etat(d.getEtat() != null ? d.getEtat().name() : null)
                .priorite(d.getPriorite())
                .dateSoumission(d.getDateSoumission())
                .dateLimite(d.getDateLimite())
                .clientId(d.getClient().getId())
                .clientCode(d.getClient().getCode())
                .clientRaisonSociale(d.getClient().getRaisonSociale())
                .services(serviceDTOs)
                .build();
    }

    // ─── Get current client profile ───────────────────────────────────────────
    public UserDTO getProfile(String code) {
        User client = findClientByCode(code);
        return toUserDTO(client);
    }

    // ─── Update client profile ────────────────────────────────────────────────
    public UserDTO updateProfile(String code, ClientProfileRequest request) {
        User client = findClientByCode(code);

        // Update contact info if provided
        if (request.getNom() != null && !request.getNom().isBlank())
            client.setNom(request.getNom().trim());

        if (request.getPrenom() != null && !request.getPrenom().isBlank())
            client.setPrenom(request.getPrenom().trim());

        if (request.getEmail() != null && !request.getEmail().isBlank())
            client.setEmail(request.getEmail().trim());

        if (request.getTelephone() != null)
            client.setTelephone(request.getTelephone().trim());

        // Update company info if provided
        if (request.getAdresseSiege() != null && !request.getAdresseSiege().isBlank())
            client.setAdresseSiege(request.getAdresseSiege().trim());

        if (request.getTelephoneEntreprise() != null)
            client.setTelephoneEntreprise(request.getTelephoneEntreprise().trim());

        // Password change — only if both fields provided
        if (request.getCurrentPassword() != null && !request.getCurrentPassword().isBlank()
                && request.getNewPassword() != null && !request.getNewPassword().isBlank()) {

            if (!passwordEncoder.matches(request.getCurrentPassword(), client.getMotDePasse())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
            }

            client.setMotDePasse(passwordEncoder.encode(request.getNewPassword()));
        }

        try {
            return toUserDTO(userRepository.save(client));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }
    }

    // ─── Helper: User entity → UserDTO ───────────────────────────────────────
    private UserDTO toUserDTO(User user) {
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