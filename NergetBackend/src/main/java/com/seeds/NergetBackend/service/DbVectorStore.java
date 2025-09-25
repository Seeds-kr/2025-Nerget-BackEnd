// src/main/java/com/seeds/NergetBackend/service/DbVectorStore.java
package com.seeds.NergetBackend.service;

import com.seeds.NergetBackend.dto.CandidateImageDto;
import com.seeds.NergetBackend.entity.ImageVector;
import com.seeds.NergetBackend.repository.ImageVectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Primary
@Service
@RequiredArgsConstructor
public class DbVectorStore implements VectorStore {

    private final ImageVectorRepository imageVectorRepository;

    /** 후보 이미지를 count개 랜덤 제공 */
    @Override
    public List<CandidateImageDto> getRandomCandidates(int count) {
        List<ImageVector> all = imageVectorRepository.findByStatus(ImageVector.Status.DONE);
        if (all.isEmpty()) return Collections.emptyList();

        Collections.shuffle(all);
        return all.stream()
                .limit(count)
                .map(iv -> new CandidateImageDto(iv.getId(), iv.getS3Key()))
                .collect(Collectors.toList());
    }

    /** 선택한 이미지 ID들에 대한 벡터 반환 */
    @Override
    public List<float[]> getVectorsByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        return imageVectorRepository.findByIdIn(ids).stream()
                .map(ImageVector::toArray)
                .collect(Collectors.toList());
    }
}