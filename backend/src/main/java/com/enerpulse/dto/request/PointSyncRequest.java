package com.enerpulse.dto.request;

import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

@Data
public class PointSyncRequest {
    private Long gatewayId;
    @Valid
    private List<PointSyncItem> points;
}
