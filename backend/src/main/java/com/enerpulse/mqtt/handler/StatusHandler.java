package com.enerpulse.mqtt.handler;

import com.enerpulse.entity.Gateway;
import com.enerpulse.repository.GatewayRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatusHandler {
    private final ObjectMapper objectMapper;
    private final GatewayRepository gatewayRepository;

    public void handle(String payload) throws Exception {
        JsonNode root = objectMapper.readTree(payload);
        String gatewayCode = root.path("gatewayId").asText();
        String status = root.path("status").asText();

        Gateway gateway = gatewayRepository.findByGatewayCode(gatewayCode).orElse(null);
        if (gateway == null) {
            log.warn("Unknown gateway: {}", gatewayCode);
            return;
        }
        gateway.setStatus(status);
        if ("ONLINE".equals(status)) {
            gateway.setLastOnlineAt(OffsetDateTime.now());
        }
        gatewayRepository.save(gateway);
        log.info("Gateway {} status -> {}", gatewayCode, status);
    }
}
