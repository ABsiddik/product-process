package com.xyz.api.service;

import com.xyz.api.config.KafkaConfig;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class KafkaProducerService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(String key, Object payload) {
        kafkaTemplate.send(KafkaConfig.TOPIC, key, payload);
    }
    public void sendToDlq(String key, Object payload) {
        kafkaTemplate.send(KafkaConfig.DLQ, key, payload);
    }
}
