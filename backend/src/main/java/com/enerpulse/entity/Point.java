package com.enerpulse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "points", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"gateway_id", "point_code"})
})
public class Point extends BaseEntity {
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "gateway_id", nullable = false)
    private Long gatewayId;

    @Column(name = "device_id")
    private Long deviceId;

    @Column(name = "point_code", nullable = false, length = 64)
    private String pointCode;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(name = "energy_type_id")
    private Long energyTypeId;

    @Column(name = "energy_item_id")
    private Long energyItemId;

    @Column(name = "value_type", nullable = false, length = 32)
    private String valueType;

    @Column(length = 32)
    private String unit;

    @Column(name = "total_flag", nullable = false)
    private Boolean totalFlag = false;

    @Column(nullable = false, precision = 20, scale = 6)
    private BigDecimal multiplier = BigDecimal.ONE;

    @Column(nullable = false, length = 32)
    private String status = "ACTIVE";

    @Column(length = 500)
    private String description;
}
