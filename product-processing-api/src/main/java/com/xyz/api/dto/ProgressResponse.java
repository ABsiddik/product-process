package com.xyz.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ProgressResponse {
    private String batchId;
    private String state;
    private long total;
    private long processed;
    private String message;
}
