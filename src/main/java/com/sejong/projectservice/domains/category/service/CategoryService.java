package com.sejong.projectservice.domains.category.service;

import static com.sejong.projectservice.support.common.exception.ExceptionType.CATEGORY_NOT_FOUND;

import com.sejong.projectservice.domains.category.domain.CategoryEntity;
import com.sejong.projectservice.domains.category.dto.CategoryAllResponse;
import com.sejong.projectservice.domains.category.dto.CategoryResponse;
import com.sejong.projectservice.domains.category.repository.CategoryRepository;
import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import com.sejong.projectservice.domains.project.repository.ProjectRepository;
import com.sejong.projectservice.support.common.constants.Type;
import com.sejong.projectservice.support.common.exception.BaseException;
import com.sejong.projectservice.support.common.exception.ExceptionType;
import com.sejong.projectservice.support.common.file.FileUploader;
import com.sejong.projectservice.support.outbox.OutboxEventRequest;
import com.sejong.projectservice.support.outbox.OutboxService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private static final String CATEGORY_ICON_DIR = "project-service/category-icon";
    private final CategoryRepository categoryRepository;
    private final ProjectRepository projectRepository;
    private final OutboxService outboxService;
    private final FileUploader fileUploader;

    @Transactional
    public CategoryResponse create(String userRole, String name, String description, String content, String iconKey) {

        validateAdminRole(userRole);
        CategoryEntity categoryEntity = CategoryEntity.of(name, description, content, null);
        CategoryEntity savedCategoryEntity = categoryRepository.save(categoryEntity);

        if (iconKey != null && !iconKey.isBlank()) {
            String targetDir = String.format("%s/%d", CATEGORY_ICON_DIR, savedCategoryEntity.getId());
            String finalKey = fileUploader.moveFile(iconKey, targetDir);
            savedCategoryEntity.updateIconKey(finalKey);
        }
        return CategoryResponse.from(savedCategoryEntity);
    }

    @Transactional
    public CategoryResponse update(String userRole, String name, String description,
                                   String content, String iconKey) {
        validateAdminRole(userRole);
        CategoryEntity categoryEntity = categoryRepository.findByName(name)
                .orElseThrow(() -> new BaseException(CATEGORY_NOT_FOUND));
        categoryEntity.updateDescription(description);
        categoryEntity.updateContent(content);

        if (iconKey != null && !iconKey.isBlank()) {
            if (categoryEntity.getIconKey() != null) {
                try {
                    fileUploader.delete(categoryEntity.getIconKey());
                } catch (Exception e) {
                    // 기존 아이콘 삭제 실패 시 계속 진행
                }
            }
            String targetDir = String.format("%s/%d", CATEGORY_ICON_DIR, categoryEntity.getId());
            String finalKey = fileUploader.moveFile(iconKey, targetDir);
            categoryEntity.updateIconKey(finalKey);
        }
        return CategoryResponse.updateFrom(categoryEntity);
    }

    @Transactional
    public CategoryResponse remove(String userRole, String name) {
        validateAdminRole(userRole);
        CategoryEntity categoryEntity = categoryRepository.findByName(name)
                .orElseThrow(() -> new BaseException(CATEGORY_NOT_FOUND));
        categoryRepository.deleteById(categoryEntity.getId());
        return CategoryResponse.deleteFrom(categoryEntity);
    }

    @Transactional(readOnly = true)
    public CategoryAllResponse getAll() {
        List<CategoryEntity> categories = categoryRepository.findAll();
        return CategoryAllResponse.from(categories, fileUploader);
    }

    @Transactional
    public CategoryAllResponse updateProject(String username, Long projectId, List<String> categoryNames) {
        ProjectEntity projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException(ExceptionType.PROJECT_NOT_FOUND));
        projectEntity.validateUserPermission(username);

        List<CategoryEntity> categories = categoryNames.stream().map(
                c -> categoryRepository.findByName(c).orElseThrow(() -> new BaseException(CATEGORY_NOT_FOUND))
        ).toList();

        projectEntity.updateCategory(categories);
        OutboxEventRequest outbox = OutboxEventRequest.of(projectEntity, fileUploader, Type.UPDATED);
        outboxService.enqueue(outbox);
        return CategoryAllResponse.from(categories, fileUploader);
    }

    private void validateAdminRole(String userRole) {
        if (!userRole.equals("ADMIN")) {
            throw new BaseException(ExceptionType.REQUIRED_ADMIN);
        }
    }

    @Transactional
    public CategoryResponse updateDescription(String userRole, Long categoryId, String description, String content) {
        CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BaseException(CATEGORY_NOT_FOUND));
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
