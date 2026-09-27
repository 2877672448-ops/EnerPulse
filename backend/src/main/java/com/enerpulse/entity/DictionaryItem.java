package com.enerpulse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "dictionary_items", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tenant_id", "type_id", "code"})
})
public class DictionaryItem extends BaseEntity {
    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "type_id", nullable = false)
    private Long typeId;

    @Column(nullable = false, length = 64)
    private String code;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(length = 32)
    private String unit;

    @Column(name = "sort_no", nullable = false)
    private Integer sortNo = 0;

    @Column(nullable = false, length = 32)
    private String status = "ACTIVE";
}
