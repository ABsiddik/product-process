package com.xyz.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xyz.api.dto.ProductDto;
import com.xyz.api.dto.ProductListRequest;
import com.xyz.api.dto.ProductRequest;
import com.xyz.api.dto.ProductResponse;
import com.xyz.api.entity.Product;
import com.xyz.api.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Value("${app.data.storage}")
    private String dataDir;

    public boolean isExistBySKU(String sku) {
        return productRepository.existsBySku(sku);
    }

    public void exportAsJson(ProductResponse product, ObjectMapper objectMapper) throws Exception {
        Path uploadPath = Paths.get(dataDir);
        String sku = product.getSku();
        Path productPath = uploadPath.resolve(sku).resolve(sku + ".json");

        File productFile = productPath.toFile();
        if (!productFile.exists()) {
            productFile.getParentFile().mkdirs();
            productFile.createNewFile();
        }

        objectMapper.writeValue(productFile, product);

        saveProduct(sku, productFile.getPath());
    }

    private void saveProduct(String sku, String jsonPath) {
        Product product = new Product();
        product.setSku(sku);
        product.setJsonPath(jsonPath);

        productRepository.save(product);
    }

    public List<ProductResponse> findProducts(Pageable pageable) {
        try {
            List<ProductResponse> list = new ArrayList<>();
            ObjectMapper objectMapper = new ObjectMapper();
            Page<Product> products = productRepository.findAll(pageable);
            for (Product product : products.toList()) {
                File json = new File(product.getJsonPath());
                ProductResponse res = objectMapper.readValue(json, ProductResponse.class);
                list.add(res);
            }
            return list;
        } catch (Exception e) {
            log.error("Error while retrieving products - {}", e.getMessage());
        }

        return List.of();
    }
}
