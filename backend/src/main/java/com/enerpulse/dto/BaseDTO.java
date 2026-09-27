package com.enerpulse.dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class BaseDTO {
    private Long id;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
