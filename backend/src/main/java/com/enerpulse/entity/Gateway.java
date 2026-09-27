package com.enerpulse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "gateways", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tenant_id", "gateway_code"}),
        @UniqueConstraint(columnNames = {"tenant_id", "topic"})
})
public class Gateway extends BaseEntity {
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "gateway_code", nullable = false, length = 64)
    private String gatewayCode;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(nullable = false, length = 255)
    private String topic;

    @Column(name = "server_host", nullable = false, length = 255)
    private String serverHost;

    @Column(name = "server_port", nullable = false)
    private Integer serverPort = 1883;

    @Column(name = "report_interval", nullable = false)
    private Integer reportInterval = 60;

    @Column(nullable = false, length = 32)
    private String status = "OFFLINE";

    @Column(name = "last_online_at")
    private OffsetDateTime lastOnlineAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;
}
