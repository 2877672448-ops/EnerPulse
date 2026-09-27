package com.enerpulse.dto.response;

import com.enerpulse.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.OffsetDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class GatewayResponse extends BaseDTO {
    private Long tenantId;
    private String gatewayCode;
    private String name;
    private String topic;
    private String serverHost;
    private Integer serverPort;
    private Integer reportInterval;
    private String status;
    private OffsetDateTime lastOnlineAt;
}
