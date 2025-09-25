package com.seeds.NergetBackend.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class EmbeddingService {

    private static final int DIM = 4; // 실제는 512/768 등

    /** 단일 이미지 URI를 벡터로 변환 (모킹) */
    public float[] embed(String uri) {
        float[] v = new float[DIM];
        Random r = new Random(uri.hashCode());
        for (int i = 0; i < DIM; i++) {
            v[i] = (float) r.nextGaussian();
        }
        return v;
    }

    /** 여러 이미지 임베딩 */
    public List<float[]> embedAll(List<String> uris) {
        List<float[]> out = new ArrayList<>();
        for (String uri : uris) out.add(embed(uri));
        return out;
    }

    /** 간단한 평균 집계 */
    public float[] aggregate(List<float[]> vectors) {
        int dim = 4; // 4차원 고정
        float[] sum = new float[dim];
        if (vectors == null || vectors.isEmpty()) return sum;
            for (float[] v : vectors) {
                for (int i = 0; i < dim; i++) {
                    float val = (v != null && v.length > i) ? v[i] : 0f;
                    sum[i] += val;
                }
            }
            for (int i = 0; i < dim; i++) sum[i] /= vectors.size();
            return sum;
    }
}