package com.enerpulse.dto.request;

import lombok.Data;

import java.time.LocalTime;

@Data
public class TariffPeriodRequest {
    private String name;
    private String periodType;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer sortNo;
    private String status;
}
