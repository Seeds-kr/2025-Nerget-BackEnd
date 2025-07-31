package com.seeds.NergetBackend.repository;

import com.seeds.NergetBackend.entity.UserStyleVector;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserStyleVectorRepository extends JpaRepository<UserStyleVector, Integer> {
    Optional<UserStyleVector> findByMemberId(Integer memberId);
}