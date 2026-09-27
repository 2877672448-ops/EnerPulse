package com.enerpulse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "roles", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tenant_id", "code"})
})
public class Role extends BaseEntity {
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(nullable = false, length = 64)
    private String name;

    @Column(nullable = false, length = 64)
    private String code;

    @Column(length = 255)
    private String description;

    @Column(nullable = false, length = 32)
    private String status = "ACTIVE";
}
