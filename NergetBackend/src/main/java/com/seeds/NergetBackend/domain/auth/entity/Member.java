package com.seeds.NergetBackend.domain.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "members")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer memberId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String password; // 소셜 로그인이라면 null 가능

    @Column(length = 50)
    private String nickname;


    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}