package com.enerpulse.dto.response;

import com.enerpulse.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DeviceResponse extends BaseDTO {
    private Long tenantId;
    private String deviceCode;
    private String name;
    private Long gatewayId;
    private Long areaId;
    private Long energyTypeId;
    private String installLocation;
    private String status;
    private String description;
}
