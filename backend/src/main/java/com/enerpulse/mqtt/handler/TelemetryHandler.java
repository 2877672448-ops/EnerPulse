package com.enerpulse.mqtt.handler;

import com.enerpulse.entity.EnergyRawData;
import com.enerpulse.entity.Gateway;
import com.enerpulse.entity.Point;
import com.enerpulse.repository.EnergyRawDataRepository;
import com.enerpulse.repository.GatewayRepository;
import com.enerpulse.repository.PointRepository;
import com.enerpulse.service.AlarmRuleService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelemetryHandler {
    private final ObjectMapper objectMapper;
    private final GatewayRepository gatewayRepository;
    private final PointRepository pointRepository;
    private final EnergyRawDataRepository rawDataRepository;
    private final AlarmRuleService alarmRuleService;

    public void handle(String payload) throws Exception {
        JsonNode root = objectMapper.readTree(payload);
        String gatewayCode = root.path("gatewayId").asText();
        String messageId = root.path("messageId").asText(null);
        OffsetDateTime timestamp = OffsetDateTime.parse(root.path("timestamp").asText());

        Gateway gateway = gatewayRepository.findByGatewayCode(gatewayCode).orElse(null);
        if (gateway == null) {
            log.warn("Unknown gateway: {}", gatewayCode);
            return;
        }

        JsonNode points = root.path("points");
        if (points.isArray()) {
            for (JsonNode p : points) {
                String pointCode = p.path("pointId").asText();
                BigDecimal value = p.path("value").decimalValue();
                String quality = p.path("quality").asText("GOOD");

                Point point = pointRepository.findByGatewayIdAndPointCode(gateway.getId(), pointCode).orElse(null);
                if (point == null) {
                    log.warn("Unknown point {}/{}", gatewayCode, pointCode);
                    continue;
                }

                // Idempotency: messageId + pointId
                if (messageId != null) {
                    if (rawDataRepository.findByMessageIdAndPointId(messageId, point.getId()).isPresent()) {
                        log.debug("Duplicate message {} for point {}", messageId, point.getId());
                        continue;
                    }
                }

                EnergyRawData raw = new EnergyRawData();
                raw.setTenantId(gateway.getTenantId());
                raw.setGatewayId(gateway.getId());
                raw.setDeviceId(point.getDeviceId());
                raw.setPointId(point.getId());
                raw.setTs(timestamp);
                raw.setValue(value.multiply(point.getMultiplier()));
                raw.setQuality(quality);
                raw.setUnit(point.getUnit());
                raw.setMessageId(messageId);
                Map<String, Object> rp = new HashMap<>();
                rp.put("gatewayId", gatewayCode);
                raw.setRawPayload(rp);
                raw.setReceivedAt(OffsetDateTime.now());
                rawDataRepository.save(raw);
            }
        }
    }
}
