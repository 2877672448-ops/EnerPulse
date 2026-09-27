package com.enerpulse.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AlarmRequest {
    private Long deviceId;
    private Long pointId;
    private String alarmCode;
    private String alarmType;
    private String level;
    private String message;
    private BigDecimal value;
    private BigDecimal threshold;
}
