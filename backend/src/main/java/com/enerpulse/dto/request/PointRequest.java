package com.enerpulse.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PointRequest {
    private Long gatewayId;
    private Long deviceId;
    @NotBlank
    private String pointCode;
    @NotBlank
    private String name;
    private Long energyTypeId;
    private Long energyItemId;
    @NotBlank
    private String valueType;
    private String unit;
    private Boolean totalFlag;
    private BigDecimal multiplier;
    private String status;
    private String description;
}
