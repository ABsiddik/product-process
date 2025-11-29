package com.xyz.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xyz.api.dto.ProductListRequest;
import com.xyz.api.dto.ProductMessage;
import com.xyz.api.dto.ProductRequest;
import com.xyz.api.dto.ProductResponse;
import com.xyz.api.utils.FileUploadUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@AllArgsConstructor
@Slf4j
@Service
public class ProductProcessService {

    private final KafkaProducerService producer;
    private final FileUploadUtils fileUploadUtils;

    @Async("appExecutor")
    public void processProducts(List<ProductRequest> products, String batchId) {
        try {
            for (ProductRequest product : products) {
                ProductMessage message = convertToMessage(product);
                message.setBatchId(batchId);

                producer.send(product.getSku(), message);
            }
        } catch (Exception ex) {
            log.error("error while processing - {}", ex.getMessage());
        }
    }

    private ProductMessage convertToMessage(ProductRequest req) throws IOException {
        ProductResponse product = new ProductResponse();
        BeanUtils.copyProperties(req, product);

        List<String> imagesPaths = fileUploadUtils.uploadMultipartFiles(req.getPhotos(), req.getSku());
        product.setImagesPaths(imagesPaths);

        ProductMessage message = new ProductMessage();
        message.setProduct(product);

        return message;
    }
}
