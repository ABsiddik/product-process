package com.xyz.api.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xyz.api.config.KafkaConfig;
import com.xyz.api.dto.ProductMessage;
import com.xyz.api.dto.ProductResponse;
import com.xyz.api.service.KafkaProducerService;
import com.xyz.api.service.ProductProcessService;
import com.xyz.api.service.ProductProgressService;
import com.xyz.api.service.ProductService;
import com.xyz.api.utils.FileUploadUtils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Slf4j
@Component
public class ProductConsumer {

    private final ProductService productService;
    private final KafkaProducerService producerService;
    private final ProductProgressService progressService;
    private final FileUploadUtils fileUploadUtils;

    @KafkaListener(topics = KafkaConfig.TOPIC, groupId = "${app.kafka.group:product-consumers}", containerFactory = "kafkaListenerContainerFactory")
    @Transactional
    public void listen(ProductMessage msg) {
        ProductResponse product = msg.getProduct();
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            if (!productService.isExistBySKU(product.getSku())) {
                productService.exportAsJson(product, objectMapper);
            } else {
                log.warn("SKU is already exist - {}", product.getSku());
            }

            progressService.updateByOne(msg.getBatchId());
        } catch (Exception ex) {
            producerService.sendToDlq(product.getSku(), msg);
            progressService.failed(msg.getBatchId(), ex.getMessage());
            fileUploadUtils.deleteDir(product.getSku());
            log.error("Error while consuming product - {}", ex.getMessage());
        }
    }
}
