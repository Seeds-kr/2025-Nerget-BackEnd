package com.seeds.NergetBackend.repository;

import com.seeds.NergetBackend.entity.SwipeFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SwipeFeedbackRepository extends JpaRepository<SwipeFeedback, Long> {
}