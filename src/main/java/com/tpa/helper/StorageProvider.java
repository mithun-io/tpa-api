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
            Path targetLocation = this.uploadPath.resolve(fileName);

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

        String contentType = file.getContentType();

        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new BadRequestException("Invalid file type. Only PDF, JPG and PNG are allowed");
        }

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        if (fileName.contains("..")) {
            throw new BadRequestException("Invalid file name");
        }
    }
}