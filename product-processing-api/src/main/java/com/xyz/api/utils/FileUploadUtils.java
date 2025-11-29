package com.xyz.api.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class FileUploadUtils {
    @Value("${app.data.storage}")
    private String dataDir;

    public void createBaseDir() throws IOException {
        Path uploadPath = Paths.get(dataDir);
        Files.createDirectories(uploadPath);
    }

    public List<String> uploadMultipartFiles(List<MultipartFile> images, String dir) throws IOException {
        if (images == null || images.isEmpty())
            return List.of();

        Path uploadPath = Paths.get(dataDir);
        Path productDir = uploadPath.resolve(dir);
        if (!productDir.toFile().exists()) {
            productDir.toFile().mkdirs();
        }

        List<String> paths = new ArrayList<>();
        for (MultipartFile file : images) {
            if (!file.isEmpty()) {
                String originalFilename = file.getOriginalFilename();
                Path filePath = productDir.resolve(originalFilename);
                file.transferTo(filePath);
                paths.add(filePath.toString());
            }
        }

        return paths;
    }

    public void deleteDir(String dir) {
        Path uploadPath = Paths.get(dataDir);
        Path productDir = uploadPath.resolve(dir);
        try {
            Files.deleteIfExists(productDir);
        } catch (Exception e) {
            log.error("Error while deleting directory: {}, cause: {}", productDir, e.getMessage());
        }
    }
}
