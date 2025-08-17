
package com.seeds.NergetBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter @AllArgsConstructor
public class PageResponse<T> {
    private final List<T> items;
    private final Long nextCursor; // 다음 페이지 id (없으면 null)
}

