package com.seeds.NergetBackend.global.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class LocalFileService {

    @Value("${app.upload.dir:uploads/images}")
    private String uploadDir;

    /**
     * 로컬에 파일 저장
     */
    public String saveFile(MultipartFile file) throws IOException {
        // 업로드 디렉토리 생성
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 파일명 생성: timestamp_uuid.확장자
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";
        
        String filename = System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;
        
        // 파일 저장
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return filename;
    }

    /**
     * 파일 URL 생성
     */
    public String getFileUrl(String filename) {
        return "/uploads/images/" + filename;
    }

    /**
     * 파일 리소스 로드
     */
    public Resource loadFileAsResource(String filename) throws IOException {
        Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
        Resource resource = new UrlResource(filePath.toUri());
        
        if (resource.exists()) {
            return resource;
        } else {
            throw new IOException("File not found: " + filename);
        }
    }

    /**
     * 샘플 이미지 URL 생성 (static/images)
     */
    public String getSampleImageUrl(String filename) {
        return "/images/" + filename;
    }
}

