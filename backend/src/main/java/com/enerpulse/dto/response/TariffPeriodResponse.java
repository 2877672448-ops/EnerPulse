package com.enerpulse.dto.response;

import com.enerpulse.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class TariffPeriodResponse extends BaseDTO {
    private Long tenantId;
    private String name;
    private String periodType;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer sortNo;
    private String status;
}
