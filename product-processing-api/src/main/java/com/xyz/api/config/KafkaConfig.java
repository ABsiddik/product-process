package com.xyz.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {
    public static final String TOPIC = "product-topic";
    public static final String DLQ = "product-dlq";

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
}
