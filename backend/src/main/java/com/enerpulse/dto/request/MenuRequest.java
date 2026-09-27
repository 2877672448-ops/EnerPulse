package com.enerpulse.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MenuRequest {
    private Long parentId;
    @NotBlank
    private String name;
    private String path;
    private String icon;
    private Integer sortNo;
    private String status;
    private String permissionCode;
    private Boolean publicAccess;
}
