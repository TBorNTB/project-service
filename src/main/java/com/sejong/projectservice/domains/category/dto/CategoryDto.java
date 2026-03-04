package com.sejong.projectservice.domains.category.dto;

import java.util.List;

import com.sejong.projectservice.domains.category.domain.CategoryEntity;
import com.sejong.projectservice.support.common.file.FileUploader;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDto {

    private Long id;
    private String name;
    private String description;
    private String content;
    private String iconUrl;

    public static CategoryDto of(String name) {
        return CategoryDto.builder()
                .id(null)
                .name(name)
                .build();
    }

    public static List<CategoryDto> fromList(List<CategoryEntity> categoryEntities) {
        return categoryEntities.stream()
                .map(it -> CategoryDto.builder()
                        .id(it.getId())
                        .name(it.getName())
                        .description(it.getDescription())
                        .content(it.getContent())
                        .iconUrl(null)
                        .build())
                .toList();
    }

    public static List<CategoryDto> fromList(List<CategoryEntity> categoryEntities, FileUploader fileUploader) {
        return categoryEntities.stream()
                .map(it -> CategoryDto.builder()
                        .id(it.getId())
                        .name(it.getName())
                        .description(it.getDescription())
                        .content(it.getContent())
                        .iconUrl(it.getIconKey() != null ? fileUploader.getFileUrl(it.getIconKey()) : null)
                        .build())
                .toList();
    }
}
