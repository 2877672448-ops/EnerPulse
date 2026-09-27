package com.enerpulse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "alarms", indexes = {
        @Index(name = "idx_alarms_tenant_occurred", columnList = "tenant_id, occurred_at")
})
public class Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "gateway_id")
    private Long gatewayId;

    @Column(name = "device_id")
    private Long deviceId;

    @Column(name = "point_id")
    private Long pointId;

    @Column(name = "alarm_code", nullable = false, length = 64)
    private String alarmCode;

    @Column(name = "alarm_type", length = 64)
    private String alarmType;

    @Column(nullable = false, length = 16)
    private String level;

    @Column(length = 500)
    private String message;

    @Column(name = "alarm_value", precision = 30, scale = 10)
    private BigDecimal value;

    @Column(precision = 30, scale = 10)
    private BigDecimal threshold;

    @Column(nullable = false, length = 32)
    private String status = "ACTIVE";

    @Column(name = "occurred_at", nullable = false)
    private OffsetDateTime occurredAt;

    @Column(name = "recovered_at")
    private OffsetDateTime recoveredAt;

    @Column(name = "ack_by")
    private Long ackBy;

    @Column(name = "ack_at")
    private OffsetDateTime ackAt;

    @Column(name = "closed_by")
    private Long closedBy;

    @Column(name = "closed_at")
    private OffsetDateTime closedAt;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
