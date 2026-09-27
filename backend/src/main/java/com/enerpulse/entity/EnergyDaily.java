package com.enerpulse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "energy_daily", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tenant_id", "point_id", "period_date"})
})
public class EnergyDaily {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "point_id")
    private Long pointId;

    @Column(name = "device_id")
    private Long deviceId;

    @Column(name = "area_id")
    private Long areaId;

    @Column(name = "energy_type_id")
    private Long energyTypeId;

    @Column(name = "period_date", nullable = false)
    private LocalDate periodDate;

    @Column(nullable = false, precision = 20, scale = 6)
    private BigDecimal consumption = BigDecimal.ZERO;

    @Column(nullable = false, precision = 20, scale = 6)
    private BigDecimal cost = BigDecimal.ZERO;

    @Column(name = "data_quality", nullable = false, length = 32)
    private String dataQuality = "GOOD";
}
