package com.enerpulse.mqtt;

import com.enerpulse.mqtt.config.MqttConfig;
import com.enerpulse.mqtt.handler.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MqttConsumer implements MqttCallback {
    private final MqttConfig mqttConfig;
    private final TelemetryHandler telemetryHandler;
    private final StatusHandler statusHandler;
    private final AlarmHandler alarmHandler;
    private final AckHandler ackHandler;

    @PostConstruct
    public void init() throws Exception {
        mqttConfig.subscribe("enerpulse/+/+/+", this);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        try {
            String payload = new String(message.getPayload());
            log.debug("MQTT received topic={} payload={}", topic, payload);
            String[] parts = topic.split("/");
            if (parts.length != 4) {
                log.warn("Invalid topic: {}", topic);
                return;
            }
            String type = parts[3];
            switch (type) {
                case "telemetry" -> telemetryHandler.handle(payload);
                case "status" -> statusHandler.handle(payload);
                case "alarm" -> alarmHandler.handle(payload);
                case "ack" -> ackHandler.handle(payload);
                default -> log.debug("Ignored topic type: {}", type);
            }
        } catch (Exception e) {
            log.error("MQTT dispatch error for topic {}", topic, e);
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        log.warn("MQTT connection lost", cause);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }
}
