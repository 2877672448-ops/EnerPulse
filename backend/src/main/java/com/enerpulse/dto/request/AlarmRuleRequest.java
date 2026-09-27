package com.enerpulse.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AlarmRuleRequest {
    @NotNull
    private Long pointId;
    @NotBlank
    private String ruleName;
    @NotBlank
    private String condition;
    @NotNull
    private BigDecimal threshold;
    private String level;
    private Boolean enabled;
}
