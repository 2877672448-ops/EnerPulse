package com.enerpulse.dto.response;

import com.enerpulse.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RoleResponse extends BaseDTO {
    private Long tenantId;
    private String name;
    private String code;
    private String description;
    private String status;
}
