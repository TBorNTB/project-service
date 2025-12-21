package com.sejong.projectservice.domains.category.repository;

import com.sejong.projectservice.domains.category.domain.Category;

import java.util.List;

public interface CategoryRepository {
    Category save(String name);

    Category findByName(String name);

    Category update(String prevName, String nextName);

    Category delete(String name);

    List<Category> findAll();

    Category updateDescription(Category category);

    Category findOne(Long categoryId);
}
