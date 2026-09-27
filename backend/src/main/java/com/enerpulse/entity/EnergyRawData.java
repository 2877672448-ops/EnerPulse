package com.enerpulse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "energy_raw_data", indexes = {
        @Index(name = "idx_energy_raw_point_ts", columnList = "point_id, ts"),
        @Index(name = "idx_energy_raw_device_ts", columnList = "device_id, ts"),
        @Index(name = "idx_energy_raw_gateway_ts", columnList = "gateway_id, ts")
})
public class EnergyRawData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "gateway_id", nullable = false)
    private Long gatewayId;

    @Column(name = "device_id")
    private Long deviceId;

    @Column(name = "point_id", nullable = false)
    private Long pointId;

    @Column(nullable = false)
    private OffsetDateTime ts;

    @Column(name = "raw_value", nullable = false, precision = 30, scale = 10)
    private BigDecimal value;

    @Column(nullable = false, length = 32)
    private String quality = "GOOD";

    @Column(length = 32)
    private String unit;

    @Column(name = "message_id", length = 128)
    private String messageId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "raw_payload", columnDefinition = "jsonb")
    private Map<String, Object> rawPayload;

    @Column(name = "received_at", nullable = false)
    private OffsetDateTime receivedAt = OffsetDateTime.now();
}
