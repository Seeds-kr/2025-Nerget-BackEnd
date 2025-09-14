// src/main/java/com/seeds/NergetBackend/dto/SurveyScores.java
package com.seeds.NergetBackend.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SurveyScores {
    /** S(+)/B(-), F(+)/C(-), G(+)/P(-), E(+)/N(-)  범위: [-1.0, +1.0] */
    private float s; // S vs B
    private float f; // F vs C
    private float g; // G vs P
    private float e; // E vs N

    public float[] toArray() { return new float[]{s, f, g, e}; }
}