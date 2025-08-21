package com.seeds.NergetBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PageResponseDto<T> {
    private final List<T> items;
    private final Long nextCursor;
}

