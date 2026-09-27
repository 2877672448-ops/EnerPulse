package com.enerpulse.dto.response;

import com.enerpulse.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class TariffResponse extends BaseDTO {
    private Long tenantId;
    private Long energyTypeId;
    private Long tariffPeriodId;
    private BigDecimal price;
    private String currency;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private String status;
}
