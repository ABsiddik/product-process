package com.xyz.api.controller;

import com.xyz.api.dto.ProductListRequest;
import com.xyz.api.service.ProductProgressService;
import com.xyz.api.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class ProductController {

    private final ProductProgressService progressService;
    private final ProductService productService;

    @PostMapping(value = "/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> processProducts(@ModelAttribute ProductListRequest listRequest) {
        String batchId = UUID.randomUUID().toString();
        progressService.start(batchId, listRequest.getProducts().size());
        productService.processProducts(listRequest, batchId);
        return ResponseEntity.ok("Bulk uploading");
    }
}
