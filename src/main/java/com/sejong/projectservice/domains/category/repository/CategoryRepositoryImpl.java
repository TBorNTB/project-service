package com.sejong.projectservice.domains.category.repository;

import com.sejong.projectservice.support.common.error.code.ErrorCode;
import com.sejong.projectservice.support.common.error.exception.ApiException;
import com.sejong.projectservice.domains.category.domain.Category;
import com.sejong.projectservice.domains.category.domain.CategoryEntity;
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

    @Override
    public List<Category> findAll() {
        List<CategoryEntity> categoryEntities = jpaRepository.findAll();
        return categoryEntities.stream()
                .map(CategoryEntity::toDomain)
                .toList();
    }

    @Override
    public Category updateDescription(Category category) {
        CategoryEntity categoryEntity = jpaRepository.findById(category.getId())
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "해당 이름은 카테고리 목록에 없습니다."));

        categoryEntity.updateDescription(category.getDescription());
        CategoryEntity updatedCategoryEntity = jpaRepository.save(categoryEntity);
        return updatedCategoryEntity.toDomain();
    }

    @Override
    public Category findOne(Long categoryId) {
        CategoryEntity categoryEntity = jpaRepository.findById(categoryId)
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "해당 이름은 카테고리 목록에 없습니다."));
        return categoryEntity.toDomain();
    }

}
