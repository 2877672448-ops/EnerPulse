package com.enerpulse.mqtt.handler;

import com.enerpulse.entity.Alarm;
import com.enerpulse.entity.Gateway;
import com.enerpulse.repository.AlarmRepository;
import com.enerpulse.repository.GatewayRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlarmHandler {
    private final ObjectMapper objectMapper;
    private final GatewayRepository gatewayRepository;
    private final AlarmRepository alarmRepository;

    public void handle(String payload) throws Exception {
        JsonNode root = objectMapper.readTree(payload);
        String gatewayCode = root.path("gatewayId").asText();

        Gateway gateway = gatewayRepository.findByGatewayCode(gatewayCode).orElse(null);
        if (gateway == null) {
            log.warn("Unknown gateway for alarm: {}", gatewayCode);
            return;
        }

        Alarm alarm = new Alarm();
        alarm.setTenantId(gateway.getTenantId());
        alarm.setGatewayId(gateway.getId());
        alarm.setAlarmCode(root.path("alarmCode").asText());
        alarm.setAlarmType(root.path("alarmType").asText(null));
        alarm.setLevel(root.path("level").asText("HIGH"));
        alarm.setMessage(root.path("message").asText(null));
        if (root.has("value")) alarm.setValue(root.path("value").decimalValue());
        if (root.has("threshold")) alarm.setThreshold(root.path("threshold").decimalValue());
        alarm.setStatus(root.path("status").asText("ACTIVE"));
        alarm.setOccurredAt(OffsetDateTime.parse(root.path("timestamp").asText()));
        alarmRepository.save(alarm);
        log.info("Alarm received: {} - {}", alarm.getAlarmCode(), alarm.getMessage());
    }
}
