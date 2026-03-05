package com.sejong.projectservice.domains.category.dto.event;

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
public class CategoryPayload {

    private Long id;
    private String name;

    private String description;
    private String content;
    private String iconUrl;

    public static CategoryPayload from(CategoryEntity category, FileUploader fileUploader) {
        String iconUrl = category.getIconKey() != null && !category.getIconKey().isEmpty()
                ? fileUploader.getFileUrl(category.getIconKey())
                : null;

        return CategoryPayload.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .content(category.getContent())
                .iconUrl(iconUrl)
                .build();
    }
}
