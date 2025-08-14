package com.sejong.projectservice.application.category.controller.dto;

import com.sejong.projectservice.core.category.Category;
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
    List<Category> categories;

    public static CategoryAllResponse from(List<Category> categories) {
        return CategoryAllResponse.builder().categories(categories).build();
    }
}
