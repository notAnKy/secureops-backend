package com.cyberplatform.backend.service;

import com.cyberplatform.backend.dto.RapportDTO;
import com.cyberplatform.backend.entity.Rapport;
import com.cyberplatform.backend.entity.User;
import com.cyberplatform.backend.repository.RapportRepository;
import com.cyberplatform.backend.repository.TacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminReportService {

    private final RapportRepository rapportRepository;
    private final TacheRepository tacheRepository;
    private final EmailService emailService;

    // ─── Get all reports for a task ───────────────────────────────────────────
    public List<RapportDTO> getReportsByTask(Long taskId) {
        tacheRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Task not found"));

        return rapportRepository.findByTacheIdTache(taskId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ─── Get all reports for a request ────────────────────────────────────────
    public List<RapportDTO> getReportsByRequest(Long demandeId) {
        return rapportRepository.findByDemandeIdDemande(demandeId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ─── Validate a report — sends email to client ────────────────────────────
    public RapportDTO validateReport(Long rapportId) {
        Rapport rapport = rapportRepository.findById(rapportId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Report not found"));

        rapport.setEstValide(true);
        RapportDTO saved = toDTO(rapportRepository.save(rapport));

        // Send email notification to client — never fail validation if email fails
        try {
            if (rapport.getDemande() != null && rapport.getDemande().getClient() != null) {
                User client    = rapport.getDemande().getClient();
                String email   = client.getEmail();
                String name    = client.getRaisonSociale() != null
                        ? client.getRaisonSociale()
                        : (client.getPrenom() + " " + client.getNom()).trim();
                Long requestId = rapport.getDemande().getIdDemande();
                String content = rapport.getContenu();

                emailService.sendReportValidated(email, name, requestId, content);
            }
        } catch (Exception e) {
            log.error("Email notification failed for report {}: {}", rapportId, e.getMessage());
        }

        return saved;
    }

    // ─── Invalidate a report — sends email to client ──────────────────────────
    public RapportDTO invalidateReport(Long rapportId) {
        Rapport rapport = rapportRepository.findById(rapportId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Report not found"));

        rapport.setEstValide(false);
        RapportDTO saved = toDTO(rapportRepository.save(rapport));

        // Send email notification to client — never fail invalidation if email fails
        try {
            if (rapport.getDemande() != null && rapport.getDemande().getClient() != null) {
                User client    = rapport.getDemande().getClient();
                String email   = client.getEmail();
                String name    = client.getRaisonSociale() != null
                        ? client.getRaisonSociale()
                        : (client.getPrenom() + " " + client.getNom()).trim();
                Long requestId = rapport.getDemande().getIdDemande();

                emailService.sendReportInvalidated(email, name, requestId);
            }
        } catch (Exception e) {
            log.error("Email notification failed for report {}: {}", rapportId, e.getMessage());
        }

        return saved;
    }

    // ─── toDTO ────────────────────────────────────────────────────────────────
    public RapportDTO toDTO(Rapport r) {
        return RapportDTO.builder()
                .idRapport(r.getIdRapport())
                .contenu(r.getContenu())
                .dateSoumission(r.getDateSoumission())
                .estValide(r.getEstValide())
                .employeId(r.getEmploye() != null ? r.getEmploye().getId() : null)
                .employeCode(r.getEmploye() != null ? r.getEmploye().getCode() : null)
                .employeNom(r.getEmploye() != null ? r.getEmploye().getNom() : null)
                .employePrenom(r.getEmploye() != null ? r.getEmploye().getPrenom() : null)
                .tacheId(r.getTache() != null ? r.getTache().getIdTache() : null)
                .demandeId(r.getDemande() != null ? r.getDemande().getIdDemande() : null)
                .build();
    }
}