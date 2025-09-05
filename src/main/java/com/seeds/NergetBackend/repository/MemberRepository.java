package com.seeds.NergetBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.seeds.NergetBackend.entity.Member;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {

    boolean existsByNicknameAndIdNot(String nickname, Long id);

    Optional<Member> findByEmail(String email);
}