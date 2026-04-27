package com.cyberplatform.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatsDTO {

    // Users
    private long totalUsers;
    private long totalClients;
    private long totalEmployees;

    // Requests
    private long totalRequests;
    private long pendingRequests;
    private long inProgressRequests;
    private long completedRequests;
    private long cancelledRequests;

    // Services
    private long totalServices;
}