// src/main/java/com/seeds/NergetBackend/repository/ImageInteractionRepository.java
package com.seeds.NergetBackend.repository;

import com.seeds.NergetBackend.entity.ImageInteraction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageInteractionRepository extends JpaRepository<ImageInteraction, Long> {
    Optional<ImageInteraction> findByMemberIdAndImageVectorId(Long memberId, String imageVectorId);
    long countByMemberId(Long memberId);
}