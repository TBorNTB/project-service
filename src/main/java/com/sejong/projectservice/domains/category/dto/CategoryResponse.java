package com.sejong.projectservice.domains.category.dto;

import com.sejong.projectservice.domains.category.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryResponse {
    private Long id;
    private String name;
    private String message;

    public static CategoryResponse from(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .message("카테고리 생성 완료")
                .build();
    }

    public static CategoryResponse updateFrom(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .message("카테고리 수정 완료")
                .build();
    }
    public static CategoryResponse deleteFrom(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .message("카테고리 삭제 완료")
                .build();
    }
}
