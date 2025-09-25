// src/main/java/com/seeds/NergetBackend/repository/MemberMbtiRepository.java
package com.seeds.NergetBackend.domain.choice.repository;

import com.seeds.NergetBackend.domain.choice.entity.MemberMbti;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberMbtiRepository extends JpaRepository<MemberMbti, Long> {
}