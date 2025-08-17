package com.seeds.NergetBackend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "post_likes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "member_id"})
)
@Getter @Setter @NoArgsConstructor
public class PostLike {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
