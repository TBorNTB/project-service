package com.sejong.projectservice.domains.category.dto;

import com.sejong.projectservice.domains.category.domain.CategoryEntity;
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

    public static CategoryResponse from(CategoryEntity categoryEntity) {
        return CategoryResponse.builder()
                .id(categoryEntity.getId())
                .name(categoryEntity.getName())
                .message("카테고리 생성 완료")
                .build();
    }

    public static CategoryResponse updateFrom(CategoryEntity categoryEntity) {
        return CategoryResponse.builder()
                .id(categoryEntity.getId())
                .name(categoryEntity.getName())
                .message("카테고리 수정 완료")
                .build();
    }
    public static CategoryResponse deleteFrom(CategoryEntity categoryEntity) {
        return CategoryResponse.builder()
                .id(categoryEntity.getId())
                .name(categoryEntity.getName())
                .message("카테고리 삭제 완료")
                .build();
    }
}
