package com.sejong.projectservice.core.category;

import java.util.List;

public interface CategoryRepository {
    Category save( String name);

    Category findByName(String name);

    Category update(String prevName, String nextName);

    Category delete(String name);

    List<Category> findAll();

    Category updateDescription(Category category);

    Category findOne(Long categoryId);
}
