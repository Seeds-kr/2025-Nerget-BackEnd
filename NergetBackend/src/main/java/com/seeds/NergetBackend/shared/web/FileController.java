package com.seeds.NergetBackend.shared.web;

import com.seeds.NergetBackend.shared.storage.LocalFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class FileController {

    private final LocalFileService localFileService;

    @GetMapping("/uploads/images/{filename:.+}")
    public ResponseEntity<Resource> serveUploadedFile(
            @PathVariable String filename) {
        try {
            Resource resource = localFileService.loadFileAsResource(filename);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

