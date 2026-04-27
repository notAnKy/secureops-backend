package com.cyberplatform.backend.service;

import com.cyberplatform.backend.dto.DemandeDTO;
import com.cyberplatform.backend.dto.PagedResponse;
import com.cyberplatform.backend.dto.ServiceDTO;
import com.cyberplatform.backend.dto.UpdateStatusRequest;
import com.cyberplatform.backend.entity.Demande;
import com.cyberplatform.backend.entity.enums.RequestStatus;
import com.cyberplatform.backend.repository.DemandeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminRequestService {

    private final DemandeRepository demandeRepository;

    // ─── Get all requests with pagination (optionally filtered by status) ─────
    public PagedResponse<DemandeDTO> getAllRequests(RequestStatus etat, int page, int size) {
        PageRequest pageable = PageRequest.of(
            page, size, Sort.by(Sort.Direction.DESC, "dateSoumission")
        );

        Page<Demande> result = (etat != null)
            ? demandeRepository.findByEtat(etat, pageable)
            : demandeRepository.findAll(pageable);

        List<DemandeDTO> content = result.getContent()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return PagedResponse.<DemandeDTO>builder()
                .content(content)
                .currentPage(result.getNumber())
                .totalPages(result.getTotalPages())
                .totalElements(result.getTotalElements())
                .size(result.getSize())
                .first(result.isFirst())
                .last(result.isLast())
                .build();
    }

    // ─── Get one request by ID ────────────────────────────────────────────────
    public DemandeDTO getRequestById(Long id) {
        Demande demande = demandeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Request not found with id: " + id));
        return toDTO(demande);
    }

    // ─── Update request status ────────────────────────────────────────────────
    public DemandeDTO updateStatus(Long id, UpdateStatusRequest request) {
        Demande demande = demandeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Request not found with id: " + id));

        if (request.getEtat() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required");
        }

        demande.setEtat(request.getEtat());
        return toDTO(demandeRepository.save(demande));
    }

    // ─── Helper: entity → DTO ─────────────────────────────────────────────────
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
}