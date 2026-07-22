package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Service
public class FileUploadService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    private static final List<String> ALLOWED_EXTENSIONS = 
        Arrays.asList("jpg", "jpeg", "png", "webp", "gif");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    public String uploadFile(MultipartFile file) throws IOException {

        // Dosya boş mu?
        if (file.isEmpty()) {
            throw new RuntimeException("Dosya boş");
        }

        // Boyut kontrolü
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("Dosya boyutu 5MB'dan büyük olamaz");
        }

        // Uzantı kontrolü
        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new RuntimeException(
                "Geçersiz dosya formatı. İzin verilen: " + ALLOWED_EXTENSIONS
            );
        }

        // Klasörü oluştur
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Benzersiz dosya adı
        String filename = UUID.randomUUID() + "." + extension;
        Path filePath = uploadPath.resolve(filename);

        // Dosyayı kaydet
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return "/images/" + filename;
    }

    public List<String> uploadFiles(MultipartFile[] files) throws IOException {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                urls.add(uploadFile(file));
            }
        }
        return urls;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}