package com.enerpulse.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PointSyncItem {
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
    private String description;
}
