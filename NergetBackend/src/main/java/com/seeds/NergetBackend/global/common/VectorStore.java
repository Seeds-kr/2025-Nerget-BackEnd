package com.seeds.NergetBackend.global.common;

import com.seeds.NergetBackend.domain.candidate.dto.CandidateImageDto;
import java.util.List;

public interface VectorStore {
    /** 후보 이미지를 count개 랜덤으로 제공 (실제에선 벡터DB/목록에서 샘플링) */
    List<CandidateImageDto> getRandomCandidates(int count);

    /** 선택한 이미지 ID들에 대한 벡터를 반환 */
    List<float[]> getVectorsByIds(List<String> ids);
}