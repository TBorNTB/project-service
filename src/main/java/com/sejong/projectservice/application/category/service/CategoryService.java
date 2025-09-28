package com.sejong.projectservice.application.category.service;

import com.sejong.projectservice.application.category.controller.dto.CategoryAllResponse;
import com.sejong.projectservice.application.category.controller.dto.CategoryResponse;
import com.sejong.projectservice.application.common.error.code.ErrorCode;
import com.sejong.projectservice.application.common.error.exception.ApiException;
import com.sejong.projectservice.core.category.Category;
import com.sejong.projectservice.core.category.CategoryRepository;
import com.sejong.projectservice.core.project.domain.Project;
import com.sejong.projectservice.core.project.repository.ProjectRepository;
import jakarta.validation.constraints.NotBlank;
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
        Category category = categoryRepository.save(name);
        return CategoryResponse.from(category);
    }

    @Transactional
    public CategoryResponse update(String userRole, String prevName, String nextName) {
        validateAdminRole(userRole);
        Category category = categoryRepository.update(prevName, nextName);
        return CategoryResponse.updateFrom(category);
    }

    @Transactional
    public CategoryResponse remove(String userRole, String name) {
        validateAdminRole(userRole);
        Category category = categoryRepository.delete(name);
        return CategoryResponse.deleteFrom(category);
    }

    @Transactional(readOnly = true)
    public CategoryAllResponse getAll() {
        List<Category> categories = categoryRepository.findAll();
        return CategoryAllResponse.from(categories);
    }

    @Transactional
    public CategoryAllResponse updateProject( String userName,Long projectId, List<String> categoryNames) {
        Project project = projectRepository.findOne(projectId);
        project.validateUserPermission(userName);
        project.updateCategory(categoryNames);

        Project updatedProject = projectRepository.update(project);
        return CategoryAllResponse.from(updatedProject.getCategories());
    }

    private void validateAdminRole(String userRole) {
        if(!userRole.equals("ADMIN")){
            throw new ApiException(ErrorCode.BAD_REQUEST,"관리자만 가능합니다.");
        }
    }
}
