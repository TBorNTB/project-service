package com.sejong.projectservice.domains.category.service;

import com.sejong.projectservice.domains.category.domain.CategoryEntity;
import com.sejong.projectservice.domains.category.dto.CategoryAllResponse;
import com.sejong.projectservice.domains.category.dto.CategoryResponse;
import com.sejong.projectservice.domains.category.repository.CategoryRepository;
import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import com.sejong.projectservice.domains.project.repository.ProjectRepository;
import com.sejong.projectservice.support.common.exception.BaseException;
import com.sejong.projectservice.support.common.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public CategoryResponse create(String userRole, String name, String description, String content) {

        validateAdminRole(userRole);
        CategoryEntity categoryEntity = CategoryEntity.of(name, description, content);
        CategoryEntity savedCategoryEntity = categoryRepository.save(categoryEntity);
        return CategoryResponse.from(savedCategoryEntity);
    }

    @Transactional
    public CategoryResponse update(String userRole, String prevName, String nextName, String description, String content) {
        validateAdminRole(userRole);
        CategoryEntity categoryEntity = categoryRepository.findByName(prevName)
                .orElseThrow(() -> new BaseException(ExceptionType.CATEGORY_NOT_FOUND));
        categoryEntity.updateName(nextName);
        categoryEntity.updateDescription(description);
        categoryEntity.updateContent(content);
        return CategoryResponse.updateFrom(categoryEntity);
    }

    @Transactional
    public CategoryResponse remove(String userRole, String name) {
        validateAdminRole(userRole);
        CategoryEntity categoryEntity = categoryRepository.findByName(name)
                .orElseThrow(() -> new BaseException(ExceptionType.CATEGORY_NOT_FOUND));
        categoryRepository.deleteById(categoryEntity.getId());
        return CategoryResponse.deleteFrom(categoryEntity);
    }

    @Transactional(readOnly = true)
    public CategoryAllResponse getAll() {
        List<CategoryEntity> categories = categoryRepository.findAll();
        return CategoryAllResponse.from(categories);
    }

    @Transactional
    public CategoryAllResponse updateProject(String username, Long projectId, List<String> categoryNames) {
        ProjectEntity projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException(ExceptionType.PROJECT_NOT_FOUND));
        projectEntity.validateUserPermission(username);

        List<CategoryEntity> categoryEntities = categoryNames.stream()
                .map(CategoryEntity::of).toList();

        categoryRepository.saveAll(categoryEntities);
        projectEntity.updateCategory(categoryNames, categoryEntities);

        return CategoryAllResponse.from(categoryEntities);
    }

    private void validateAdminRole(String userRole) {
        if (!userRole.equals("ADMIN")) {
            throw new BaseException(ExceptionType.REQUIRED_ADMIN);
        }
    }

    @Transactional
    public CategoryResponse updateDescription(String userRole, Long categoryId, String description, String content) {
        CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BaseException(ExceptionType.CATEGORY_NOT_FOUND));
        ;
        categoryEntity.updateDescription(description);
        categoryEntity.updateContent(content);
        return CategoryResponse.updateFrom(categoryEntity);

    }

    @Transactional(readOnly = true)
    public Long getCategoryCount() {
        Long count = categoryRepository.findAllCategoryCount();
        return count;
    }
}
