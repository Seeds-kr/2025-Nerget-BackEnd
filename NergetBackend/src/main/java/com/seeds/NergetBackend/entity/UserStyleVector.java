package com.seeds.NergetBackend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_style_vectors")
@Data
public class UserStyleVector {

    @Id
    private Integer memberId; // FK (members.id)

    @Column(columnDefinition = "TEXT")
    private String vectorData;

    private LocalDateTime updatedAt;
}