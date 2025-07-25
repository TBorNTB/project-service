package com.sejong.projectservice.core.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageResult<T> {
    private List<T> content;
    private int size;
    private int number;
    private int totalPages;
    private long totalElements;

    public static <T> PageResult<T> from(List<T> content, int size, int number, int totalPages, long totalElements) {
        return PageResult.<T>builder()
                .content(content)
                .size(size)
                .number(number)
                .totalPages(totalPages)
                .totalElements(totalElements)
                .build();
    }
}
