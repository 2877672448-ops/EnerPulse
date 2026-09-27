package com.enerpulse.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TariffRequest {
    private Long energyTypeId;
    private Long tariffPeriodId;
    private BigDecimal price;
    private String currency;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private String status;
}
