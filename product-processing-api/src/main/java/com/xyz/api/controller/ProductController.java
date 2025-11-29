package com.xyz.api.controller;

import com.xyz.api.dto.ProductListRequest;
import com.xyz.api.dto.ProgressResponse;
import com.xyz.api.enums.ProgressStateEnum;
import com.xyz.api.service.ProductProcessService;
import com.xyz.api.service.ProductProgressService;
import com.xyz.api.service.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class ProductController {

    private final ProductProgressService progressService;
    private final ProductProcessService productProcessService;
    private final ProductService productService;

    @PostMapping(value = "/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> processProducts(@Valid @ModelAttribute ProductListRequest listRequest) {
        String batchId = UUID.randomUUID().toString();
        progressService.start(batchId, listRequest.getProducts().size());
        productProcessService.processProducts(listRequest.getProducts(), batchId);

        return ResponseEntity.ok(
                new ProgressResponse(batchId, ProgressStateEnum.RUNNING.toString(), listRequest.getProducts().size(),
                        0, "Products processing is started")
        );
    }

    @GetMapping(value = "/products/{batchId}/status")
    public ResponseEntity<?> getProcessStatus(@PathVariable String batchId) {
        ProgressResponse response = progressService.status(batchId);
        if (response != null) {
            return ResponseEntity.ok(progressService.status(batchId));
        }
        else {
            response = new ProgressResponse(batchId, null, 0, 0, "No progress data found");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/products")
    public ResponseEntity<?> getProductList(Pageable pageable) {
        return ResponseEntity.ok(productService.findProducts(pageable));
    }
}
