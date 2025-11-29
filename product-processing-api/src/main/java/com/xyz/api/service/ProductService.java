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

    public void processProducts(ProductListRequest request, String batchId) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Path uploadPath = Paths.get(dataDir);
            Files.createDirectories(uploadPath);

            List<ProductRequest> products = request.getProducts();
            for (ProductRequest product : products) {
                if (productRepository.existsBySku(product.getSku()))
                    continue;

                exportProductAsJson(product, uploadPath, objectMapper);
            }
        } catch (Exception ex) {
            log.error("error while processing - {}", ex.getMessage());
        }
    }

    private void exportProductAsJson(ProductRequest req, Path uploadPath, ObjectMapper objectMapper) throws Exception {
        String sku = req.getSku();
        Path productDir = uploadPath.resolve(sku);

        Path productPath = productDir.resolve(sku + ".json");
        File productFile = productPath.toFile();
        if (!productFile.exists()) {
            productFile.getParentFile().mkdirs();
            productFile.createNewFile();
        }

        List<String> imagesPaths = uploadFiles(req.getPhotos(), productDir);

        ProductResponse product = new ProductResponse();
        BeanUtils.copyProperties(req, product);

        product.setImagesPaths(imagesPaths);

        objectMapper.writeValue(productFile, product);

        saveProduct(sku, productFile.getPath());
    }

    private List<String> uploadFiles(List<MultipartFile> images, Path dir) throws IOException {
        if (images == null || images.isEmpty())
            return List.of();

        List<String> paths = new ArrayList<>();
        for (MultipartFile file : images) {
            if (!file.isEmpty()) {
                String originalFilename = file.getOriginalFilename();
                Path filePath = dir.resolve(originalFilename);
                file.transferTo(filePath);
                paths.add(filePath.toString());
            }
        }

        return paths;
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
