package com.seeds.NergetBackend.global.common;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class StorageService {

    /**
     * 실제 환경: S3/로컬에 저장 후 퍼블릭/서명 URL 반환
     * 지금은 flowId 기반의 모킹 URI를 만듭니다.
     */
    public List<String> saveInitial(String flowId, List<MultipartFile> files) {
        return IntStream.range(0, files.size())
                .mapToObj(i -> "mock://" + flowId + "/initial/" + i)
                .toList();
    }
}