package com.seeds.NergetBackend.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.seeds.NergetBackend.domain.auth.entity.Member; // ✅ 우리가 만든 Member 엔티티
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findByEmail(String email);
}