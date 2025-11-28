package com.xyz.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductListRequest {
    private List<ProductRequest> products;
}
