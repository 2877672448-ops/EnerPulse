package com.enerpulse.dto.response;

import com.enerpulse.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class AlarmResponse extends BaseDTO {
    private Long tenantId;
    private Long deviceId;
    private Long pointId;
    private String alarmCode;
    private String alarmType;
    private String level;
    private String message;
    private BigDecimal value;
    private BigDecimal threshold;
    private String status;
    private OffsetDateTime occurredAt;
    private OffsetDateTime recoveredAt;
    private Long ackBy;
    private OffsetDateTime ackAt;
    private OffsetDateTime closedAt;
}
