package com.enerpulse.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DictionaryItemRequest {
    private Long typeId;
    @NotBlank
    private String code;
    @NotBlank
    private String name;
    private String unit;
    private Integer sortNo;
    private String status;
}
