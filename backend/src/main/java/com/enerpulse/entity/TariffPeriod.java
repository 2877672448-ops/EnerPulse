package com.enerpulse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "tariff_periods")
public class TariffPeriod extends BaseEntity {
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(nullable = false, length = 64)
    private String name;

    @Column(name = "period_type", nullable = false, length = 16)
    private String periodType;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "sort_no", nullable = false)
    private Integer sortNo;

    @Column(nullable = false, length = 32)
    private String status = "ACTIVE";
}
