package com.enerpulse.dto.response;

import com.enerpulse.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.OffsetDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class OperationLogResponse extends BaseDTO {
    private Long tenantId;
    private Long userId;
    private String action;
    private String module;
    private String targetType;
    private String targetId;
    private String ip;
    private String detail;
}
