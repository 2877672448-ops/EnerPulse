package com.enerpulse.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String code;
    private String description;
    private String status;
}
