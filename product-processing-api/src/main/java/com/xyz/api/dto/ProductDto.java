package com.xyz.api.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDto {
    private String sku;
    private String name;
    private BigDecimal price;
    private String description;
    private String category;
    private String brand;
}
