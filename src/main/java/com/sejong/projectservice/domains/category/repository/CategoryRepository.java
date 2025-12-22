package com.sejong.projectservice.domains.category.repository;

import com.sejong.projectservice.domains.category.domain.CategoryDto;

import java.util.List;

public interface CategoryRepository {
    CategoryDto save(String name);

    CategoryDto findByName(String name);

    CategoryDto update(String prevName, String nextName);

    CategoryDto delete(String name);

    List<CategoryDto> findAll();

    CategoryDto updateDescription(CategoryDto categoryDto);

    CategoryDto findOne(Long categoryId);
}
