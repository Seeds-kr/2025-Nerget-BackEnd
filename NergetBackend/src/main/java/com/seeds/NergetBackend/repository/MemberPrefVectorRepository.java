// src/main/java/com/seeds/NergetBackend/repository/MemberPrefVectorRepository.java
package com.seeds.NergetBackend.repository;

import com.seeds.NergetBackend.entity.MemberPrefVector;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberPrefVectorRepository extends JpaRepository<MemberPrefVector, Long> {
}