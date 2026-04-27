package com.cyberplatform.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserStatsDTO {
    private Long userId;
    private String role;

    // For clients
    private long totalRequests;
    private long activeRequests; // PENDING or IN_PROGRESS

    // For employees
    private long totalTasks;
    private long activeTasks; // NOT_STARTED or IN_PROGRESS
}