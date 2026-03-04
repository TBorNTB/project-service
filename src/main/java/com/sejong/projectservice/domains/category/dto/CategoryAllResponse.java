package com.sejong.projectservice.domains.category.dto;

import com.sejong.projectservice.domains.category.domain.CategoryEntity;
import com.sejong.projectservice.support.common.file.FileUploader;
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
        return new CategoryAllResponse(CategoryDto.fromList(categories));
    }

    public static CategoryAllResponse from(List<CategoryEntity> categories, FileUploader fileUploader) {
        return new CategoryAllResponse(CategoryDto.fromList(categories, fileUploader));
    }
}
