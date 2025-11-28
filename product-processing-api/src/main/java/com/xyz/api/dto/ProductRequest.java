package com.xyz.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProductRequest extends ProductDto {
    private List<MultipartFile> photos;
}
