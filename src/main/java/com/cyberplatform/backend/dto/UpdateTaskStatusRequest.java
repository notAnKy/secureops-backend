package com.cyberplatform.backend.dto;

import com.cyberplatform.backend.entity.enums.TaskStatus;
import lombok.Data;

@Data
public class UpdateTaskStatusRequest {
    private TaskStatus statut;
}