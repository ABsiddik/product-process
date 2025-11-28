package com.xyz.api.controller;

import com.xyz.api.dto.ProductListRequest;
import com.xyz.api.service.ProductProgressService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class ProductController {

    private final ProductProgressService progressService;

    @PostMapping("/products")
    public ResponseEntity<?> processProducts(@RequestBody ProductListRequest listRequest) {
        String batchId = UUID.randomUUID().toString();
        System.out.println(batchId);
        progressService.start(batchId, listRequest.getProducts().size());
        return ResponseEntity.ok(listRequest);
    }
}
