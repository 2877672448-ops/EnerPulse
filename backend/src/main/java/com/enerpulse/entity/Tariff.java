package com.enerpulse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "tariffs")
public class Tariff extends BaseEntity {
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "energy_type_id", nullable = false)
    private Long energyTypeId;

    @Column(name = "tariff_period_id", nullable = false)
    private Long tariffPeriodId;

    @Column(nullable = false, precision = 18, scale = 6)
    private BigDecimal price;

    @Column(nullable = false, length = 16)
    private String currency = "CNY";

    @Column(name = "effective_from", nullable = false)
    private OffsetDateTime effectiveFrom;

    @Column(name = "effective_to")
    private OffsetDateTime effectiveTo;

    @Column(nullable = false, length = 32)
    private String status = "ACTIVE";
}
