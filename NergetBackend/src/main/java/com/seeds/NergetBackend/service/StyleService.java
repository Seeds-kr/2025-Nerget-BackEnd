package com.seeds.NergetBackend.service;

import com.seeds.NergetBackend.dto.StyleAnalysisResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StyleService {
    StyleAnalysisResponseDto analyzeUploadedImages(Integer memberId, List<MultipartFile> files);
    void skipUploadAndMark(Integer memberId);
    String getMbtiForUser(Integer memberId);
}