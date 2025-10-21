package com.seeds.NergetBackend.shared.storage;

import com.seeds.NergetBackend.domain.candidate.dto.CandidateImageDto;
import com.seeds.NergetBackend.domain.flow.entity.ImageVector;
import com.seeds.NergetBackend.domain.flow.repository.ImageVectorRepository;
import com.seeds.NergetBackend.shared.vector.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class AwsVectorStoreMock implements VectorStore {

    @Autowired
    private ImageVectorRepository imageVectorRepository;

    @Override
    public List<CandidateImageDto> getRandomCandidates(int count) {
        // 실제 데이터베이스에서 랜덤 이미지 벡터들을 가져옴
        List<ImageVector> vectors = imageVectorRepository.findAll();
        List<CandidateImageDto> candidates = new ArrayList<>();
        
        // 랜덤하게 count개만 선택
        int maxCount = Math.min(count, vectors.size());
        for (int i = 0; i < maxCount; i++) {
            ImageVector vector = vectors.get(i);
            candidates.add(new CandidateImageDto(
                vector.getId(), 
                vector.getS3Key() // S3 키를 URL로 사용
            ));
        }
        
        return candidates;
    }

    @Override
    public List<float[]> getVectorsByIds(List<String> ids) {
        List<ImageVector> vectors = imageVectorRepository.findAllById(ids);
        List<float[]> result = new ArrayList<>();
        
        for (ImageVector vector : vectors) {
            result.add(vector.toArray()); // toArray() 메서드 사용
        }
        
        return result;
    }
}