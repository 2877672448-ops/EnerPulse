package com.enerpulse.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GatewayRequest {
    @NotBlank
    private String gatewayCode;
    @NotBlank
    private String name;
    private String topic;
    private String serverHost;
    private Integer serverPort;
    private Integer reportInterval;
    private String status;
}
