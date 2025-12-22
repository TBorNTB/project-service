package com.sejong.projectservice.domains.category.service;

import com.sejong.projectservice.domains.category.domain.CategoryEntity;
import com.sejong.projectservice.domains.category.dto.CategoryAllResponse;
import com.sejong.projectservice.domains.category.dto.CategoryResponse;
import com.sejong.projectservice.domains.category.repository.CategoryRepository;
import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import com.sejong.projectservice.domains.project.repository.ProjectRepository;
import com.sejong.projectservice.support.common.error.code.ErrorCode;
import com.sejong.projectservice.support.common.error.exception.ApiException;
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
    public CategoryResponse create(String userRole, String name) {
        validateAdminRole(userRole);
        CategoryEntity categoryEntity = CategoryEntity.of(name);
        CategoryEntity savedCategoryEntity = categoryRepository.save(categoryEntity);
        return CategoryResponse.from(savedCategoryEntity);
    }

    @Transactional
    public CategoryResponse update(String userRole, String prevName, String nextName) {
        validateAdminRole(userRole);
        CategoryEntity categoryEntity = categoryRepository.findByName(prevName)
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST));
        categoryEntity.updateName(nextName);
        return CategoryResponse.updateFrom(categoryEntity);
    }

    @Transactional
    public CategoryResponse remove(String userRole, String name) {
        validateAdminRole(userRole);
        CategoryEntity categoryEntity = categoryRepository.findByName(name)
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST));
        categoryRepository.deleteById(categoryEntity.getId());
        return CategoryResponse.deleteFrom(categoryEntity);
    }

    @Transactional(readOnly = true)
    public CategoryAllResponse getAll() {
        List<CategoryEntity> categories = categoryRepository.findAll();
        return CategoryAllResponse.from(categories);
    }

    @Transactional
    public CategoryAllResponse updateProject( String username,Long projectId, List<String> categoryNames) {
        ProjectEntity projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        projectEntity.validateUserPermission(username);

        List<CategoryEntity> categoryEntityEntities1 = categoryNames.stream()
                .map(CategoryEntity::of).toList();

        categoryRepository.saveAll(categoryEntityEntities1);
        projectEntity.updateCategory(categoryNames, categoryEntityEntities1);

        return CategoryAllResponse.from(categoryEntityEntities1);
    }

    private void validateAdminRole(String userRole) {
        if(!userRole.equals("ADMIN")){
            throw new ApiException(ErrorCode.BAD_REQUEST,"관리자만 가능합니다.");
        }
    }

    @Transactional
    public CategoryResponse updateDescription(String userRole, Long categoryId,String description) {
        CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "해당 이름은 카테고리 목록에 없습니다."));
        categoryEntity.updateDescription(description);
       return CategoryResponse.updateFrom(categoryEntity);

    }
}
