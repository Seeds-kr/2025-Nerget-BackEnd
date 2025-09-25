package com.seeds.NergetBackend.global.common;

import com.seeds.NergetBackend.domain.candidate.dto.CandidateImageDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class AwsVectorStoreMock implements VectorStore {

    private static final int DIM = 4; // 모킹 차원 (실제는 512/768 등)

    @Override
    public List<CandidateImageDto> getRandomCandidates(int count) {
        List<CandidateImageDto> out = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i < count; i++) {
            String id = "aws-" + Math.abs(r.nextInt());
            String url = "https://cdn.example.com/" + id + ".jpg"; // 실제: S3/CloudFront URL
            out.add(new CandidateImageDto(id, url));
        }
        return out;
    }

    @Override
    public List<float[]> getVectorsByIds(List<String> ids) {
        List<float[]> vecs = new ArrayList<>();
        for (String id : ids) vecs.add(mockVectorForId(id));
        return vecs;
    }

    private float[] mockVectorForId(String id) {
        float[] v = new float[DIM];
        Random r = new Random(id.hashCode());
        for (int i = 0; i < DIM; i++) v[i] = (float) r.nextGaussian();
        return v;
    }
}