package com.enerpulse.dto.response;

import com.enerpulse.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class EnergyRawDataResponse extends BaseDTO {
    private Long tenantId;
    private Long gatewayId;
    private Long deviceId;
    private Long pointId;
    private OffsetDateTime ts;
    private BigDecimal value;
    private String quality;
    private String unit;
}
