package com.sejong.projectservice.domains.category.dto;

import com.sejong.projectservice.domains.category.domain.CategoryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryAllResponse {
    List<CategoryDto> categories;

    public static CategoryAllResponse from(List<CategoryEntity> categories) {
        List<CategoryDto> categoryDtoList = categories.stream()
                .map(it -> {
                    return CategoryDto.builder()
                            .id(it.getId())
                            .name(it.getName())
                            .description(it.getDescription())
                            .build();
                }).toList();
        return new CategoryAllResponse(categoryDtoList);
    }
}
