// src/main/java/com/seeds/NergetBackend/repository/JobRepository.java
package com.seeds.NergetBackend.domain.flow.repository;

import com.seeds.NergetBackend.domain.flow.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, String> {

    /** 특정 사용자 작업 목록 */
    List<Job> findByUserId(String userId);

    /** 상태별 작업 조회 */
    List<Job> findByStatus(Job.Status status);

    /** 사용자와 타입으로 Job 조회 (1:1 제약 확인용) */
    Optional<Job> findByUserIdAndType(String userId, Job.Type type);
}