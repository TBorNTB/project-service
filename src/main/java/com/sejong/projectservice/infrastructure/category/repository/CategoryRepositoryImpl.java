package com.sejong.projectservice.infrastructure.category.repository;

import com.sejong.projectservice.application.common.error.code.ErrorCode;
import com.sejong.projectservice.application.common.error.exception.ApiException;
import com.sejong.projectservice.core.category.Category;
import com.sejong.projectservice.core.category.CategoryRepository;
import com.sejong.projectservice.infrastructure.category.entity.CategoryEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryJpaRepository jpaRepository;

    @Override
    public Category save(String name) {
        CategoryEntity categoryEntity = CategoryEntity.of(name);
        CategoryEntity savedEntity = jpaRepository.save(categoryEntity);
        return savedEntity.toDomain();
    }

    @Override
    public Category findByName(String name) {
        CategoryEntity categoryEntity = jpaRepository.findByName(name)
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "해당 이름은 카테고리 목록에 없습니다."));
        return categoryEntity.toDomain();
    }

    @Override
    public Category update(String prevName, String nextName) {
        CategoryEntity categoryEntity = jpaRepository.findByName(prevName)
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "해당 이름은 카테고리 목록에 없습니다."));

        categoryEntity.updateName(nextName);
        return categoryEntity.toDomain();
    }

    @Override
    public Category delete(String name) {
        CategoryEntity categoryEntity = jpaRepository.findByName(name)
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "해당 이름은 카테고리 목록에 없습니다."));
        jpaRepository.deleteById(categoryEntity.getId());
        return categoryEntity.toDomain();
    }
ㅎ
    @Override
    public List<Category> findAll() {
        List<CategoryEntity> categoryEntities = jpaRepository.findAll();
        return categoryEntities.stream()
                .map(CategoryEntity::toDomain)
                .toList();
    }
}
