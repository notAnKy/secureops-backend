package com.cyberplatform.backend.service;

import com.cyberplatform.backend.dto.ServiceDTO;
import com.cyberplatform.backend.dto.ServiceRequest;
import com.cyberplatform.backend.entity.Service;
import com.cyberplatform.backend.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

// Named AdminServiceService to avoid conflict with java.lang.Service
@Component
@RequiredArgsConstructor
public class AdminServiceService {

    private final ServiceRepository serviceRepository;

    // ─── GET ALL ──────────────────────────────────────────────────────────────
    public List<ServiceDTO> getAllServices() {
        return serviceRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ─── GET ONE ──────────────────────────────────────────────────────────────
    public ServiceDTO getServiceById(Long id) {
        return toDTO(findOrThrow(id));
    }

    // ─── CREATE ───────────────────────────────────────────────────────────────
    public ServiceDTO createService(ServiceRequest request) {
        validateRequest(request);

        Service service = Service.builder()
                .nom(request.getNom())
                .description(request.getDescription())
                .type(request.getType())
                .prix(request.getPrix())
                .build();

        return toDTO(serviceRepository.save(service));
    }

    // ─── UPDATE ───────────────────────────────────────────────────────────────
    public ServiceDTO updateService(Long id, ServiceRequest request) {
        validateRequest(request);

        Service service = findOrThrow(id);
        service.setNom(request.getNom());
        service.setDescription(request.getDescription());
        service.setType(request.getType());
        service.setPrix(request.getPrix());

        return toDTO(serviceRepository.save(service));
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────
    public void deleteService(Long id) {
        findOrThrow(id);
        serviceRepository.deleteById(id);
    }

    // ─── GET USAGE COUNT ──────────────────────────────────────────────────────
    // Returns how many requests use this service — used by frontend before delete
    public long getServiceUsage(Long id) {
        findOrThrow(id);
        return serviceRepository.countRequestsUsingService(id);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────
    private Service findOrThrow(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Service not found with id: " + id));
    }

    private void validateRequest(ServiceRequest request) {
        if (request.getNom() == null || request.getNom().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Service name is required");
        }
        if (request.getPrix() == null || request.getPrix().doubleValue() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valid price is required");
        }
    }

    public ServiceDTO toDTO(Service service) {
        return ServiceDTO.builder()
                .id(service.getId())
                .nom(service.getNom())
                .description(service.getDescription())
                .type(service.getType())
                .prix(service.getPrix())
                .createdAt(service.getCreatedAt())
                .build();
    }
}