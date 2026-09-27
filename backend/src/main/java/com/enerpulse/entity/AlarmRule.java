package com.enerpulse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "alarm_rules")
public class AlarmRule extends BaseEntity {
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "point_id", nullable = false)
    private Long pointId;

    @Column(name = "rule_name", nullable = false, length = 128)
    private String ruleName;

    @Column(nullable = false, length = 16)
    private String condition;

    @Column(nullable = false, precision = 20, scale = 6)
    private BigDecimal threshold;

    @Column(nullable = false, length = 16)
    private String level = "WARNING";

    @Column(nullable = false)
    private Boolean enabled = true;
}
