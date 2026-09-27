package com.enerpulse.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AreaRequest {
    private Long parentId;
    @NotBlank
    private String areaType;
    @NotBlank
    private String code;
    @NotBlank
    private String name;
    private Integer sortNo;
    private String status;
}
