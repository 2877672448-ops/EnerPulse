package com.enerpulse.mqtt.config;

import io.moquette.broker.Server;
import io.moquette.broker.config.MemoryConfig;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Slf4j
@Configuration
public class MqttConfig {
    @Value("${my.mqtt.broker-url}")
    private String brokerUrl;

    @Value("${my.mqtt.client-id}")
    private String clientId;

    @Value("${my.mqtt.username:}")
    private String username;

    @Value("${my.mqtt.password:}")
    private String password;

    @Value("${my.mqtt.embedded:false}")
    private boolean embedded;

    private Server embeddedServer;
    private MqttClient client;

    @PostConstruct
    public void start() throws Exception {
        if (embedded) {
            startEmbeddedBroker();
        }
        connectClient();
    }

    private void startEmbeddedBroker() throws Exception {
        log.info("Starting embedded Moquette MQTT broker on {}", brokerUrl);
        Properties props = new Properties();
        props.setProperty("port", "1883");
        props.setProperty("host", "0.0.0.0");
        props.setProperty("allow_anonymous", "true");
        embeddedServer = new Server();
        embeddedServer.startServer(new MemoryConfig(props));
        log.info("Embedded MQTT broker started");
    }

    private void connectClient() throws MqttException {
        client = new MqttClient(brokerUrl, clientId + "-" + System.currentTimeMillis());
        MqttConnectOptions opts = new MqttConnectOptions();
        opts.setAutomaticReconnect(true);
        opts.setCleanSession(true);
        if (!username.isBlank()) {
            opts.setUserName(username);
            opts.setPassword(password.toCharArray());
        }
        client.connect(opts);
        log.info("MQTT client connected to {}", brokerUrl);
    }

    public void subscribe(String topicFilter, MqttCallback callback) throws MqttException {
        client.setCallback(callback);
        client.subscribe(topicFilter, 1);
        log.info("Subscribed to {}", topicFilter);
    }

    public void publish(String topic, String payload) throws MqttException {
        MqttMessage msg = new MqttMessage(payload.getBytes());
        msg.setQos(1);
        client.publish(topic, msg);
    }

    @PreDestroy
    public void stop() {
        try {
            if (client != null && client.isConnected()) client.disconnect();
        } catch (Exception e) {
            log.warn("MQTT client disconnect failed", e);
        }
        if (embeddedServer != null) {
            embeddedServer.stopServer();
            log.info("Embedded MQTT broker stopped");
        }
    }
}
