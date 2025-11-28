package com.xyz.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProductResponse extends ProductDto {
    private List<String> imagesPaths;
}
