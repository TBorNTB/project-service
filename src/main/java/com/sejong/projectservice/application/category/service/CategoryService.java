package com.sejong.projectservice.application.category.service;

import com.sejong.projectservice.application.category.controller.dto.CategoryAllResponse;
import com.sejong.projectservice.application.category.controller.dto.CategoryResponse;
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
    public CategoryResponse create(String userId, String name) {
        //todo 이거 관리자 전용 api라서 관리자가 맞는지 검증해야 됩니다.
        Category category = categoryRepository.save(name);
        return CategoryResponse.from(category);

    }

    @Transactional
    public CategoryResponse update(String userId, String prevName, String nextName) {
        //todo 이거 관리자 전용 api라서 관리자가 맞는지 검증해야 됩니다.
        Category category = categoryRepository.update(prevName, nextName);
        return CategoryResponse.updateFrom(category);
    }

    public CategoryResponse remove(String userId, String name) {
        Category category = categoryRepository.delete(name);
        return CategoryResponse.deleteFrom(category);
    }

    @Transactional(readOnly = true)
    public CategoryAllResponse getAll() {
        List<Category> categories = categoryRepository.findAll();
        return CategoryAllResponse.from(categories);

    }

    @Transactional
    public CategoryAllResponse updateProject(String userId, Long projectId, List<String> categoryNames) {
        //todo 해당 유저가 project 수정 권한이 있는지 검증
        Project project = projectRepository.findOne(projectId);
        project.validateOwner(Long.valueOf(userId));
        project.updateCategory(categoryNames);

        Project updatedProject = projectRepository.update(project);
        return CategoryAllResponse.from(updatedProject.getCategories());
    }
}
