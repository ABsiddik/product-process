package com.xyz.api.dto;

import lombok.Data;

@Data
public class ProductMessage {
    private String batchId;
    private ProductResponse product;
}
