package com.enerpulse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "devices", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tenant_id", "device_code"})
})
public class Device extends BaseEntity {
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "device_code", nullable = false, length = 64)
    private String deviceCode;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(name = "gateway_id")
    private Long gatewayId;

    @Column(name = "area_id")
    private Long areaId;

    @Column(name = "energy_type_id")
    private Long energyTypeId;

    @Column(name = "install_location", length = 255)
    private String installLocation;

    @Column(nullable = false, length = 32)
    private String status = "ACTIVE";

    @Column(length = 500)
    private String description;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;
}
