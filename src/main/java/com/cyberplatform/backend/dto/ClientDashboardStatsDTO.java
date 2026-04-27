package com.cyberplatform.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientDashboardStatsDTO {

    // Request counts
    private long totalRequests;
    private long pendingRequests;
    private long inProgressRequests;
    private long completedRequests;
    private long cancelledRequests;

    // Recent requests (last 5)
    private List<RecentRequestDTO> recentRequests;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RecentRequestDTO {
        private Long id;
        private String description;
        private String etat;
        private String priorite;
        private String dateSoumission;
        private String dateLimite;
    }
}