package com.enerpulse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "dictionary_types", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tenant_id", "code"})
})
public class DictionaryType extends BaseEntity {
    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(nullable = false, length = 64)
    private String code;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(nullable = false, length = 32)
    private String status = "ACTIVE";
}
