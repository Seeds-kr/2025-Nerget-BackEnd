// src/main/java/com/seeds/NergetBackend/service/ChoiceService.java
package com.seeds.NergetBackend.domain.choice.service;

import com.seeds.NergetBackend.domain.choice.dto.MbtiResultDto;
import com.seeds.NergetBackend.domain.choice.dto.SurveyScores;
import com.seeds.NergetBackend.domain.choice.entity.ImageInteraction;
import com.seeds.NergetBackend.domain.flow.entity.ImageVector;
import com.seeds.NergetBackend.domain.choice.entity.MemberMbti;
import com.seeds.NergetBackend.domain.choice.entity.MemberPrefVector;
import com.seeds.NergetBackend.domain.choice.repository.ImageInteractionRepository;
import com.seeds.NergetBackend.domain.flow.repository.ImageVectorRepository;
import com.seeds.NergetBackend.domain.flow.service.JobService;
import com.seeds.NergetBackend.shared.vector.VectorStore;
import com.seeds.NergetBackend.domain.choice.repository.MemberMbtiRepository;
import com.seeds.NergetBackend.domain.choice.repository.MemberPrefVectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChoiceService {

    private final JobService jobService;                      // 초기 Job 벡터
    private final VectorStore vectorStore;                    // id 리스트 → float[4] 리스트
    private final ImageVectorRepository imageRepo;            // 이미지 벡터 접근

    // 로그/누적/MBTI 저장
    private final ImageInteractionRepository imageInteractionRepository;
    private final MemberPrefVectorRepository memberPrefVectorRepository;
    private final MemberMbtiRepository memberMbtiRepository;

    // ✅ StorageService 의존성 제거. 퍼블릭 베이스 URL을 프로퍼티로 받아 직접 조립
    // 예: https://my-bucket.s3.ap-northeast-2.amazonaws.com  또는 CDN 베이스 https://cdn.myapp.com
    @Value("${app.s3.public-base-url:}")
    private String s3PublicBaseUrl; // 비어있으면 s3Key 그대로 반환

    // 정책 스위치
    private static final float DISLIKE_SCALE = 0.0f;          // 싫어요 반영 강도(0=무시, 예:-0.2)
    private static final boolean L2_NORMALIZE = true;         // 누적 후 L2 정규화
    private static final float IMG_WEIGHT = 0.6f;             // 이미지 vs 설문 가중치
    private static final float SURVEY_WEIGHT = 0.4f;          // (설명용 상수, 계산은 IMG_WEIGHT만 사용)

    // ======================================================
    // 1) 기존: 스와이프 기반 결과 계산 (유지)
    // ======================================================
    public MbtiResultDto processChoicesFromIdsOrNull(String jobId,
                                                     List<String> likeIds,
                                                     List<String> dislikeIds) {
        com.seeds.NergetBackend.domain.flow.entity.Job job = jobService.getJob(jobId);
        if (job.getStatus() != com.seeds.NergetBackend.domain.flow.entity.Job.Status.DONE) return null;

        float[] vInit = job.toArray();
        float[] meanLike = average(vectorStore.getVectorsByIds(likeIds));
        float[] meanHate = average(vectorStore.getVectorsByIds(dislikeIds));
        float[] vSwipe = subtract(meanLike, meanHate);

        float alpha = 0.6f;
        float[] vFinal3 = combineWeighted(vInit, vSwipe, alpha);

        float v4 = polarization(vFinal3);
        float[] vFinal = new float[]{vFinal3[0], vFinal3[1], vFinal3[2], v4};
        String mbti = toMbti(vFinal);
        return new MbtiResultDto(mbti, "초기 + 스와이프 결합 결과", vFinal);
    }

    private float[] average(List<float[]> vecs) {
        int dim = 4;
        float[] avg = new float[dim];
        if (vecs == null || vecs.isEmpty()) return avg;
        for (float[] v : vecs) {
            for (int i = 0; i < dim; i++) {
                float val = (v != null && v.length > i) ? v[i] : 0f;
                avg[i] += val;
            }
        }
        for (int i = 0; i < dim; i++) avg[i] /= vecs.size();
        return avg;
    }

    private float[] combineWeighted(float[] a, float[] b, float alpha) {
        int dim = 4;
        float[] out = new float[dim];
        for (int i = 0; i < dim; i++) {
            float av = (a != null && a.length > i) ? a[i] : 0f;
            float bv = (b != null && b.length > i) ? b[i] : 0f;
            out[i] = alpha * av + (1 - alpha) * bv;
        }
        return out;
    }

    private float[] subtract(float[] a, float[] b) {
        int dim = 4;
        float[] out = new float[dim];
        for (int i = 0; i < dim; i++) {
            float av = (a != null && a.length > i) ? a[i] : 0f;
            float bv = (b != null && b.length > i) ? b[i] : 0f;
            out[i] = av - bv;
        }
        return out;
    }

    private float polarization(float[] v) {
        float pol = (Math.abs(v[0]) + Math.abs(v[1]) + Math.abs(v[2])) / 3f;
        return 2f * pol - 1f; // [-1,1]
    }

    private String toMbti(float[] v) {
        String axis1 = (v[0] >= 0 ? "S" : "B");
        String axis2 = (v[1] >= 0 ? "F" : "C");
        String axis3 = (v[2] >= 0 ? "G" : "P");
        String axis4 = (v[3] >= 0 ? "E" : "N");
        return axis1 + axis2 + axis3 + axis4;
    }

    // ======================================================
    // 2) 상호작용 로그 + 누적 벡터 갱신
    // ======================================================
    @Transactional
    public MemberPrefVector handleImageInteraction(Long memberId, String imageVectorId, int action, Double weightOpt) {
        if (action != -1 && action != 0 && action != 1) {
            throw new IllegalArgumentException("action must be -1/0/1");
        }
        double weight = (weightOpt == null) ? 1.0 : weightOpt;

        // 1) 이미지 벡터 로드
        ImageVector iv = imageRepo.findById(imageVectorId)
                .orElseThrow(() -> new IllegalArgumentException("ImageVector not found: " + imageVectorId));
        float[] x = iv.toArray();

        // 2) 상호작용 upsert
        ImageInteraction inter = imageInteractionRepository
                .findByMemberIdAndImageVectorId(memberId, imageVectorId)
                .orElse(ImageInteraction.builder()
                        .memberId(memberId)
                        .imageVectorId(imageVectorId)
                        .build());
        inter.setAction(action);
        inter.setWeight(weight);
        inter.setCreatedAt(LocalDateTime.now());
        imageInteractionRepository.save(inter);

        // 3) 누적 벡터 갱신
        MemberPrefVector pref = memberPrefVectorRepository.findById(memberId).orElseGet(() ->
                MemberPrefVector.builder()
                        .memberId(memberId)
                        .v1(0f).v2(0f).v3(0f).v4(0f)
                        .sampleCount(0)
                        .model("nerget-vision-v1")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        );

        float scale = 0f;
        if (action > 0)       scale = (float)(+1.0 * weight);
        else if (action < 0)  scale = (float)(DISLIKE_SCALE * weight);

        if (scale != 0f) {
            addScaled(pref, x, scale);
            if (L2_NORMALIZE) l2Normalize(pref);
            pref.setSampleCount(pref.getSampleCount() + 1);
        }

        return memberPrefVectorRepository.save(pref);
    }

    @Transactional
    public MemberPrefVector applyBatchInteractions(Long memberId, List<String> likeIds, List<String> dislikeIds) {
        MemberPrefVector out = null;
        if (likeIds != null) {
            for (String id : likeIds) out = handleImageInteraction(memberId, id, +1, null);
        }
        if (dislikeIds != null) {
            for (String id : dislikeIds) out = handleImageInteraction(memberId, id, -1, null);
        }
        return out;
    }

    private void addScaled(MemberPrefVector pref, float[] x, float scale) {
        if (x == null || x.length < 4) return;
        pref.setV1(pref.getV1() + x[0]*scale);
        pref.setV2(pref.getV2() + x[1]*scale);
        pref.setV3(pref.getV3() + x[2]*scale);
        pref.setV4(pref.getV4() + x[3]*scale);
    }

    private void l2Normalize(MemberPrefVector p) {
        double n = Math.sqrt(p.getV1()*p.getV1() + p.getV2()*p.getV2() + p.getV3()*p.getV3() + p.getV4()*p.getV4());
        if (n < 1e-12) return;
        p.setV1((float)(p.getV1()/n));
        p.setV2((float)(p.getV2()/n));
        p.setV3((float)(p.getV3()/n));
        p.setV4((float)(p.getV4()/n));
    }

    // ======================================================
    // 3) 이미지+설문 가중합 → 최종 MBTI 저장
    // ======================================================
    @Transactional
    public MbtiResultDto finalizeMbtiAndPersist(Long memberId,
                                                String jobId,
                                                List<String> likeIds,
                                                List<String> dislikeIds,
                                                SurveyScores survey) {
        // (선택) 로그/누적까지 같이 하고 싶으면 주석 해제
        // applyBatchInteractions(memberId, likeIds, dislikeIds);

        // 1) 이미지 기반 4차원 벡터
        float[] vImg = computeImageVector(jobId, likeIds, dislikeIds);

        // 2) 설문 4차원 벡터
        float[] vSurvey = (survey != null) ? survey.toArray() : new float[]{0,0,0,0};

        // 3) 가중합 (IMG_WEIGHT 사용)
        float[] vFinal = combineWeighted(vImg, vSurvey, IMG_WEIGHT);

        // 4) 라벨링 + 저장
        String mbti = toMbti(vFinal);
        MemberMbti row = memberMbtiRepository.findById(memberId)
                .orElse(MemberMbti.builder().memberId(memberId).build());
        row.setMbti(mbti);
        row.setSource("mixed");
        memberMbtiRepository.save(row);

        // 5) (선택) 스타일 키워드
        String keyword = styleKeywordOf(mbti);

        return new MbtiResultDto(
                mbti,
                "image(" + IMG_WEIGHT + ") + survey(" + SURVEY_WEIGHT + ") → keyword=" + keyword,
                vFinal
        );
    }

    private float[] computeImageVector(String jobId, List<String> likeIds, List<String> dislikeIds) {
        com.seeds.NergetBackend.domain.flow.entity.Job job = jobService.getJob(jobId);
        if (job.getStatus() != com.seeds.NergetBackend.domain.flow.entity.Job.Status.DONE) {
            return new float[]{0,0,0,0};
        }
        float[] vInit = job.toArray();
        float[] meanLike = average(vectorStore.getVectorsByIds(likeIds));
        float[] meanHate = average(vectorStore.getVectorsByIds(dislikeIds));
        float[] vSwipe = subtract(meanLike, meanHate);

        float alpha = 0.6f;
        float[] vFinal3 = combineWeighted(vInit, vSwipe, alpha);
        float v4 = polarization(vFinal3);
        return new float[]{vFinal3[0], vFinal3[1], vFinal3[2], v4};
    }

    private String styleKeywordOf(String mbti) {
        if (mbti == null) return "UNDEFINED";
        String k = mbti.trim();
        // S(Showy/화려), B(Basic/단조), F(Formal/미니멀), C(Comfort/맥시멀)
        // G(Glamorous/트렌디), P(Practical/클래식), E(Experimental/도전), N(Natural/안정)
        switch (k) {
            // S (Showy/화려) 계열
            case "SFGE": return "모던 시크";          // 화려·미니멀·트렌디·도전
            case "SFGN": return "세련된 모던";        // 화려·미니멀·트렌디·안정
            case "SFPE": return "엘레강스";          // 화려·미니멀·클래식·도전
            case "SFPN": return "미니멀 클래식";      // 화려·미니멀·클래식·안정
            case "SCGE": return "아방가르드";        // 화려·맥시멀·트렌디·도전
            case "SCGN": return "화려한 캐주얼";      // 화려·맥시멀·트렌디·안정
            case "SCPE": return "럭셔리 빈티지";      // 화려·맥시멀·클래식·도전
            case "SCPN": return "클래식 포멀";       // 화려·맥시멀·클래식·안정
            // B (Basic/단조) 계열
            case "BFGE": return "미니멀 트렌디";      // 단조·미니멀·트렌디·도전
            case "BFGN": return "심플 모던";         // 단조·미니멀·트렌디·안정
            case "BFPE": return "절제된 클래식";      // 단조·미니멀·클래식·도전
            case "BFPN": return "심플 베이직";       // 단조·미니멀·클래식·안정
            case "BCGE": return "보헤미안";          // 단조·맥시멀·트렌디·도전
            case "BCGN": return "캐주얼 모던";       // 단조·맥시멀·트렌디·안정
            case "BCPE": return "빈티지 믹스";       // 단조·맥시멀·클래식·도전
            case "BCPN": return "편안한 베이직";     // 단조·맥시멀·클래식·안정
            default: return "GENERIC";
        }
    }

    // ======================================================
    // 4) 홈 추천: 취향 벡터 기반 상위 N개 + URL 조립(퍼블릭 베이스)
    // ======================================================
    /**
     * 홈 피드 추천
     * 
     * 현재: DB 벡터 유사도 기반 추천
     * 실제 연동: AI 서버 POST /reco/by-user-vector 호출
     */
    public List<RecommendationItem> recommendHomeFeed(Long memberId, int limit) {
        MemberPrefVector pref = memberPrefVectorRepository.findById(memberId).orElse(null);
        if (pref == null) return List.of();

        // ============================================================
        // 현재: DB 벡터 유사도 계산 (테스트용)
        // ============================================================
        // 성능용 네이티브 쿼리 권장: findTopByPrefVector
        List<ImageVector> top;
        try {
            top = imageRepo.findTopByPrefVector(
                    pref.getV1(), pref.getV2(), pref.getV3(), pref.getV4(), limit
            );
        } catch (Exception e) {
            // 폴백: 전체(or 최신 일부) 불러 정렬 (데이터 적을 때만)
            List<ImageVector> pool = imageRepo.findAll();
            float p1 = pref.getV1(), p2 = pref.getV2(), p3 = pref.getV3(), p4 = pref.getV4();
            pool.sort((a, b) -> Float.compare(
                    dot(b, p1,p2,p3,p4),
                    dot(a, p1,p2,p3,p4)
            ));
            top = pool.subList(0, Math.min(limit, pool.size()));
        }

        float p1 = pref.getV1(), p2 = pref.getV2(), p3 = pref.getV3(), p4 = pref.getV4();
        return top.stream().map(iv -> {
            float score = dot(iv, p1,p2,p3,p4);
            String url = toPublicUrl(iv.getS3Key());
            return new RecommendationItem(iv.getId(), url, score);
        }).collect(Collectors.toList());

        // ============================================================
        // 실제 AI 서버 연동 코드 (주석 해제 후 사용)
        // ============================================================
        // try {
        //     // AI 서버에 유저 벡터 전송 → 추천 이미지 받기
        //     Map<String, Object> requestBody = Map.of(
        //         "vector", new float[]{pref.getV1(), pref.getV2(), pref.getV3(), pref.getV4()},
        //         "top_k", limit
        //     );
        //     
        //     List<Map<String, Object>> aiResponse = aiWebClient.post()
        //             .uri("/reco/by-user-vector")
        //             .bodyValue(requestBody)
        //             .retrieve()
        //             .bodyToFlux(Map.class)
        //             .collectList()
        //             .block();
        //     
        //     return aiResponse.stream()
        //             .map(item -> new RecommendationItem(
        //                 (String) item.get("image_id"),
        //                 (String) item.get("url"),
        //                 ((Number) item.get("score")).floatValue()
        //             ))
        //             .collect(Collectors.toList());
        // } catch (Exception e) {
        //     throw new RuntimeException("AI 추천 실패: " + e.getMessage(), e);
        // }
    }

    private float dot(ImageVector x, float p1, float p2, float p3, float p4) {
        return x.getV1()*p1 + x.getV2()*p2 + x.getV3()*p3 + x.getV4()*p4;
    }

    /** 퍼블릭 베이스 URL + s3Key로 최종 URL 조립 (빈 값이면 s3Key 그대로 반환) */
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

    /** 홈 추천 응답 아이템 (간단 DTO - 내부 static) */
    public static class RecommendationItem {
        public final String imageId;
        public final String imageUrl;
        public final float score;
        public RecommendationItem(String imageId, String imageUrl, float score) {
            this.imageId = imageId; this.imageUrl = imageUrl; this.score = score;
        }
    }
}