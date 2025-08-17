// src/main/java/com/seeds/NergetBackend/entity/PostLike.java
package com.seeds.NergetBackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "post_likes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"post_id","member_id"})
)
@Getter @Setter @NoArgsConstructor
public class PostLike {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="post_id", nullable=false)
    private Post post;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="member_id", nullable=false)
    private Member member; // Member.memberId가 Integer 여도 JPA가 FK로 잘 연결합니다.

    @Column(nullable=false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
