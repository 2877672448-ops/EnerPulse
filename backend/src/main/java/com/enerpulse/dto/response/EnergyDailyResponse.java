package com.enerpulse.dto.response;

import com.enerpulse.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class EnergyDailyResponse extends BaseDTO {
    private Long tenantId;
    private Long pointId;
    private Long deviceId;
    private Long areaId;
    private Long energyTypeId;
    private LocalDate periodDate;
    private BigDecimal consumption;
    private BigDecimal cost;
    private String dataQuality;
}
