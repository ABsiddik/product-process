package com.xyz.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ProductListRequest {
    @Size(min = 1, message = "At least one product is required.")
    @Valid
    private List<ProductRequest> products;
}
