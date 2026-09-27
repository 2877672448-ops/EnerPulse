package com.enerpulse.dto.response;

import com.enerpulse.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PermissionResponse extends BaseDTO {
    private String code;
    private String name;
    private String type;
    private String resource;
    private String action;
    private String description;
}
