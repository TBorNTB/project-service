package com.sejong.projectservice.domains.category.service;

import com.sejong.projectservice.domains.category.dto.CategoryAllResponse;
import com.sejong.projectservice.domains.category.dto.CategoryResponse;
import com.sejong.projectservice.support.common.error.code.ErrorCode;
import com.sejong.projectservice.support.common.error.exception.ApiException;
import com.sejong.projectservice.domains.category.domain.Category;
import com.sejong.projectservice.domains.category.repository.CategoryRepository;
import com.sejong.projectservice.domains.project.domain.Project;
import com.sejong.projectservice.domains.project.repository.ProjectRepository;
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
    public CategoryAllResponse updateProject( String username,Long projectId, List<String> categoryNames) {
        Project project = projectRepository.findOne(projectId);
        project.validateUserPermission(username);
        project.updateCategory(categoryNames);

        Project updatedProject = projectRepository.update(project);
        return CategoryAllResponse.from(updatedProject.getCategories());
    }

    private void validateAdminRole(String userRole) {
        if(!userRole.equals("ADMIN")){
            throw new ApiException(ErrorCode.BAD_REQUEST,"관리자만 가능합니다.");
        }
    }

    @Transactional
    public CategoryResponse updateDescription(String userRole, Long categoryId,String description) {
       Category category = categoryRepository.findOne(categoryId);
       category.updateDescription(description);
       Category updatedCategory = categoryRepository.updateDescription(category);
       return CategoryResponse.updateFrom(category);

    }
}
