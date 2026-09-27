package com.enerpulse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "menus")
public class Menu extends BaseEntity {
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(length = 256)
    private String path;

    @Column(length = 64)
    private String icon;

    @Column(name = "sort_no", nullable = false)
    private Integer sortNo = 0;

    @Column(nullable = false, length = 32)
    private String status = "ACTIVE";

    @Column(name = "permission_code", length = 128)
    private String permissionCode;

    @Column(name = "public_access", nullable = false)
    private Boolean publicAccess = false;

    @Column(name = "public_token", length = 128)
    private String publicToken;
}
