package com.tokoonline.service;

import com.tokoonline.config.FileUploadConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final FileUploadConfig fileUploadConfig;

    public String storeFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String filename = UUID.randomUUID().toString() + extension;

        try {
            Path targetPath = fileUploadConfig.getUploadPath().resolve(filename);
            Files.copy(file.getInputStream(), targetPath);
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Gagal menyimpan file: " + filename, e);
        }
    }

    public void deleteFile(String filename) {
        try {
            Path filePath = fileUploadConfig.getUploadPath().resolve(filename);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Gagal menghapus file: " + filename, e);
        }
    }
}