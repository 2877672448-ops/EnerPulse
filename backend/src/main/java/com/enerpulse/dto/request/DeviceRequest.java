package com.enerpulse.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeviceRequest {
    @NotBlank
    private String deviceCode;
    @NotBlank
    private String name;
    private Long gatewayId;
    private Long areaId;
    private Long energyTypeId;
    private String installLocation;
    private String status;
    private String description;
}
