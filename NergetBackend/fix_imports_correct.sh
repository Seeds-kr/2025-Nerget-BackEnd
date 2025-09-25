#!/bin/bash

echo "🔧 올바른 Import 문 수정 중..."

# 각 도메인별로 올바른 import 경로로 수정
find src/main/java/com/seeds/NergetBackend -name "*.java" -exec sed -i '' \
    -e 's/import com\.seeds\.NergetBackend\.domain\.auth\.dto\.CandidatesResponse;/import com.seeds.NergetBackend.domain.candidate.dto.CandidatesResponse;/g' \
    -e 's/import com\.seeds\.NergetBackend\.domain\.auth\.dto\.CandidateImageDto;/import com.seeds.NergetBackend.domain.candidate.dto.CandidateImageDto;/g' \
    -e 's/import com\.seeds\.NergetBackend\.domain\.auth\.service\.CandidateService;/import com.seeds.NergetBackend.domain.candidate.service.CandidateService;/g' \
    -e 's/import com\.seeds\.NergetBackend\.domain\.auth\.dto\.ChoiceRequest;/import com.seeds.NergetBackend.domain.choice.dto.ChoiceRequest;/g' \
    -e 's/import com\.seeds\.NergetBackend\.domain\.auth\.dto\.MbtiResultDto;/import com.seeds.NergetBackend.domain.choice.dto.MbtiResultDto;/g' \
    -e 's/import com\.seeds\.NergetBackend\.domain\.auth\.dto\.SurveyScores;/import com.seeds.NergetBackend.domain.choice.dto.SurveyScores;/g' \
    -e 's/import com\.seeds\.NergetBackend\.domain\.auth\.service\.ChoiceService;/import com.seeds.NergetBackend.domain.choice.service.ChoiceService;/g' \
    -e 's/import com\.seeds\.NergetBackend\.domain\.auth\.entity\.ImageInteraction;/import com.seeds.NergetBackend.domain.choice.entity.ImageInteraction;/g' \
    -e 's/import com\.seeds\.NergetBackend\.domain\.auth\.entity\.MemberMbti;/import com.seeds.NergetBackend.domain.choice.entity.MemberMbti;/g' \
    -e 's/import com\.seeds\.NergetBackend\.domain\.auth\.entity\.MemberPrefVector;/import com.seeds.NergetBackend.domain.choice.entity.MemberPrefVector;/g' \
    -e 's/import com\.seeds\.NergetBackend\.domain\.auth\.repository\.ImageInteractionRepository;/import com.seeds.NergetBackend.domain.choice.repository.ImageInteractionRepository;/g' \
    -e 's/import com\.seeds\.NergetBackend\.domain\.auth\.repository\.MemberMbtiRepository;/import com.seeds.NergetBackend.domain.choice.repository.MemberMbtiRepository;/g' \
    -e 's/import com\.seeds\.NergetBackend\.domain\.auth\.repository\.MemberPrefVectorRepository;/import com.seeds.NergetBackend.domain.choice.repository.MemberPrefVectorRepository;/g' \
    -e 's/import com\.seeds\.NergetBackend\.domain\.auth\.dto\.FlowStartResponse;/import com.seeds.NergetBackend.domain.flow.dto.FlowStartResponse;/g' \
    -e 's/import com\.seeds\.NergetBackend\.domain\.auth\.dto\.JobStartResponse;/import com.seeds.NergetBackend.domain.flow.dto.JobStartResponse;/g' \
    -e 's/import com\.seeds\.NergetBackend\.domain\.auth\.dto\.PendingDto;/import com.seeds.NergetBackend.domain.flow.dto.PendingDto;/g' \
    -e 's/import com\.seeds\.NergetBackend\.domain\.auth\.service\.JobService;/import com.seeds.NergetBackend.domain.flow.service.JobService;/g' \
    -e 's/import com\.seeds\.NergetBackend\.domain\.auth\.service\.ImageVectorService;/import com.seeds.NergetBackend.domain.flow.service.ImageVectorService;/g' \
    -e 's/import com\.seeds\.NergetBackend\.domain\.auth\.entity\.Job;/import com.seeds.NergetBackend.domain.flow.entity.Job;/g' \
    -e 's/import com\.seeds\.NergetBackend\.domain\.auth\.entity\.ImageVector;/import com.seeds.NergetBackend.domain.flow.entity.ImageVector;/g' \
    -e 's/import com\.seeds\.NergetBackend\.domain\.auth\.repository\.JobRepository;/import com.seeds.NergetBackend.domain.flow.repository.JobRepository;/g' \
    -e 's/import com\.seeds\.NergetBackend\.domain\.auth\.repository\.ImageVectorRepository;/import com.seeds.NergetBackend.domain.flow.repository.ImageVectorRepository;/g' \
    -e 's/import com\.seeds\.NergetBackend\.domain\.auth\.dto\.RecommendationItemDto;/import com.seeds.NergetBackend.domain.home.dto.RecommendationItemDto;/g' \
    -e 's/import com\.seeds\.NergetBackend\.domain\.auth\.dto\.RecommendationsResponse;/import com.seeds.NergetBackend.domain.home.dto.RecommendationsResponse;/g' \
    -e 's/import com\.seeds\.NergetBackend\.domain\.auth\.service\.VectorStore;/import com.seeds.NergetBackend.global.common.VectorStore;/g' \
    -e 's/import com\.seeds\.NergetBackend\.domain\.auth\.service\.EmbeddingService;/import com.seeds.NergetBackend.global.common.EmbeddingService;/g' \
    {} \;

echo "✅ Import 문 수정 완료!"

