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
        //todo 이거 관리자 전용 api라서 관리자가 맞는지 검증해야 됩니다.
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
        //todo 만약 프로젝트 만든 owner가 특정 유저들에게 수정권한을 부여하는 비지니스가 생긴다면
        //todo 이 코드는 손봐야 된다. 현재 유저를 찾고 그 유저가 권한이 있는지 검증해야된다.
        //todo 또한 그런 비지니스 로직이 생긴다면 전체 collaboator의 연관관계를 끊지 말고 하나하나 추가 아니면 기존게 있다면 보존 형태로 해야되겠다.
        Project project = projectRepository.findOne(projectId);
        project.validateUpdateRole(userName);
        project.updateCategory(categoryNames);

        Project updatedProject = projectRepository.update(project);
        return CategoryAllResponse.from(updatedProject.getCategories());
    }
}
