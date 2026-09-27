package com.enerpulse.dto.response;

import com.enerpulse.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class PointResponse extends BaseDTO {
    private Long tenantId;
    private Long gatewayId;
    private Long deviceId;
    private String pointCode;
    private String name;
    private Long energyTypeId;
    private Long energyItemId;
    private String valueType;
    private String unit;
    private Boolean totalFlag;
    private BigDecimal multiplier;
    private String status;
    private String description;
}
