package com.enerpulse.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PermissionRequest {
    @NotBlank
    private String code;
    @NotBlank
    private String name;
    private String type;
    private String resource;
    private String action;
    private String description;
}
