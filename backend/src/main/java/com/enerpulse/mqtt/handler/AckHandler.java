package com.enerpulse.mqtt.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AckHandler {
    private final ObjectMapper objectMapper;

    public void handle(String payload) throws Exception {
        JsonNode root = objectMapper.readTree(payload);
        String requestId = root.path("requestId").asText();
        boolean success = root.path("success").asBoolean();
        log.info("ACK received requestId={} success={} message={}",
                requestId, success, root.path("message").asText());
    }
}
