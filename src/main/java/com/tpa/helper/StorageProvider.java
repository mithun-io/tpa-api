package com.tpa.helper;

import com.tpa.exception.BadRequestException;
import com.tpa.exception.NoResourceFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class StorageProvider {

    private static final List<String> ALLOWED_CONTENT_TYPES = List.of("application/pdf", "image/jpeg", "image/png");

    private final Path uploadPath;

    public StorageProvider(@Value("${file.upload-dir}") String uploadDir) {

        this.uploadPath = Paths.get(uploadDir)
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(this.uploadPath);

        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    public String storeFile(MultipartFile multipartFile) {
        validateFile(multipartFile);

        String originalFileName = StringUtils.cleanPath(multipartFile.getOriginalFilename() != null
                ? multipartFile.getOriginalFilename()
                : "document.pdf");

        String fileName = UUID.randomUUID() + "_" + originalFileName;

        try {
            Path targetLocation = this.uploadPath.resolve(fileName).normalize();

            if (!targetLocation.startsWith(this.uploadPath)) {
                throw new BadRequestException("Path traversal attempt detected");
            }

            Files.copy(multipartFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            log.info("File stored successfully: {}", fileName);
            return targetLocation.toString();

        } catch (IOException e) {
            throw new RuntimeException("Could not store file: " + fileName, e);
        }
    }

    public Resource loadFileAsResource(String filePathStr) {
        try {
            Path storedPath = Paths.get(filePathStr);

            String fileName = storedPath.getFileName().toString();

            Path filePath = this.uploadPath.resolve(fileName).normalize();

            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            }

            throw new NoResourceFoundException("File not found: " + fileName);
        } catch (MalformedURLException ex) {
            throw new NoResourceFoundException("File not found: " + filePathStr);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }

        String fileName = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "");
        if (fileName.contains("..")) {
            throw new BadRequestException("Invalid file name");
        }

        try (java.io.InputStream is = file.getInputStream()) {
            byte[] header = new byte[8];
            if (is.read(header) < 4) {
                throw new BadRequestException("File too small");
            }

            boolean isPdf = header[0] == 0x25 && header[1] == 0x50 && header[2] == 0x44 && header[3] == 0x46;
            boolean isJpeg = (header[0] & 0xFF) == 0xFF && (header[1] & 0xFF) == 0xD8 && (header[2] & 0xFF) == 0xFF;
            boolean isPng = (header[0] & 0xFF) == 0x89 && header[1] == 0x50 && header[2] == 0x4E && header[3] == 0x47 && header[4] == 0x0D && header[5] == 0x0A && header[6] == 0x1A && header[7] == 0x0A;

            if (!isPdf && !isJpeg && !isPng) {
                throw new BadRequestException("Invalid file type. Only PDF, JPG and PNG are allowed");
            }
        } catch (IOException e) {
            throw new BadRequestException("Could not read file for validation");
        }
    }
}