package com.enerpulse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "areas", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tenant_id", "code"})
})
public class Area extends BaseEntity {
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "area_type", nullable = false, length = 32)
    private String areaType;

    @Column(nullable = false, length = 64)
    private String code;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(name = "sort_no", nullable = false)
    private Integer sortNo = 0;

    @Column(nullable = false, length = 32)
    private String status = "ACTIVE";

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;
}
