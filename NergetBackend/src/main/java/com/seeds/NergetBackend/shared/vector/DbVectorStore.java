// src/main/java/com/seeds/NergetBackend/service/DbVectorStore.java
package com.seeds.NergetBackend.shared.vector;

import com.seeds.NergetBackend.domain.candidate.dto.CandidateImageDto;
import com.seeds.NergetBackend.domain.flow.entity.ImageVector;
import com.seeds.NergetBackend.domain.flow.repository.ImageVectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Primary
@Service
@RequiredArgsConstructor
public class DbVectorStore implements VectorStore {

    private final ImageVectorRepository imageVectorRepository;
    
    // S3 퍼블릭 베이스 URL (다른 서비스들과 동일한 설정)
    @Value("${app.s3.public-base-url:}")
    private String s3PublicBaseUrl;

    /** 후보 이미지를 count개 랜덤 제공 */
    @Override
    public List<CandidateImageDto> getRandomCandidates(int count) {
        List<ImageVector> all = imageVectorRepository.findByStatus(ImageVector.Status.DONE);
        if (all.isEmpty()) return Collections.emptyList();

        Collections.shuffle(all);
        return all.stream()
                .limit(count)
                .map(iv -> new CandidateImageDto(iv.getId(), toPublicUrl(iv.getS3Key())))
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
    
    /** 퍼블릭 베이스 URL + s3Key로 최종 URL 조립 (다른 서비스들과 동일한 로직) */
    private String toPublicUrl(String s3Key) {
        if (s3Key == null) return null;
        
        // 이미 완전한 URL이면 그대로 반환
        if (s3Key.startsWith("http://") || s3Key.startsWith("https://")) {
            return s3Key;
        }
        
        String base = (s3PublicBaseUrl == null) ? "" : s3PublicBaseUrl.trim();
        if (base.isEmpty()) return s3Key; // 임시: 프론트에서 처리하게
        // base 끝/키 앞의 슬래시 중복 방지
        if (base.endsWith("/")) base = base.substring(0, base.length()-1);
        String key = s3Key.startsWith("/") ? s3Key.substring(1) : s3Key;
        return base + "/" + key;
    }
}