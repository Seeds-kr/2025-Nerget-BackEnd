package com.seeds.NergetBackend.shared.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class StorageService {

    @Value("${app.s3.public-base-url}")
    private String publicBaseUrl;

    @Value("${app.s3.bucket-name}")
    private String bucketName;

    private final S3Client s3Client;

    public StorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    /**
     * S3에 파일들을 저장하고 퍼블릭 URL 반환
     */
    public List<String> saveInitial(String flowId, List<MultipartFile> files) {
        List<String> urls = new ArrayList<>();
        
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            String s3Key = "uploads/" + flowId + "/" + filename;
            
            try {
                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3Key)
                        .contentType(file.getContentType())
                        .build();
                
                s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(
                    file.getInputStream(), file.getSize()));
                
                String publicUrl = publicBaseUrl + "/" + s3Key;
                urls.add(publicUrl);
                
            } catch (IOException e) {
                throw new RuntimeException("S3 파일 업로드 실패: " + e.getMessage(), e);
            }
        }
        
        return urls;
    }
}