package com.seeds.NergetBackend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "swipe_feedback")
@Data
public class SwipeFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer memberId;  // FK (members.id)
    private Integer styleId;   // FK (style_images.id)

    @Column(length = 10)
    private String swipeResult; // "LIKE" or "DISLIKE"

    private LocalDateTime createdAt = LocalDateTime.now();
}