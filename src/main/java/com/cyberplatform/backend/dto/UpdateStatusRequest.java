package com.cyberplatform.backend.dto;

import com.cyberplatform.backend.entity.enums.RequestStatus;
import lombok.Data;

@Data
public class UpdateStatusRequest {
    private RequestStatus etat;
}