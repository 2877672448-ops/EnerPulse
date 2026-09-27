package com.enerpulse.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DictionaryTypeRequest {
    @NotBlank
    private String code;
    @NotBlank
    private String name;
}
