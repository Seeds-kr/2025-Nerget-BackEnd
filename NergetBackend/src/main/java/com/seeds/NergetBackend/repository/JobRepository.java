// src/main/java/com/seeds/NergetBackend/repository/JobRepository.java
package com.seeds.NergetBackend.repository;

import com.seeds.NergetBackend.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, String> {

    /** 특정 사용자 작업 목록 */
    List<Job> findByUserId(String userId);

    /** 상태별 작업 조회 */
    List<Job> findByStatus(Job.Status status);
}