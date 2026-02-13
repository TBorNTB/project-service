package com.sejong.projectservice.support.common.util;

import com.sejong.projectservice.domains.category.domain.CategoryEntity;
import com.sejong.projectservice.domains.category.repository.CategoryRepository;
import com.sejong.projectservice.domains.collaborator.domain.CollaboratorEntity;
import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import com.sejong.projectservice.domains.project.dto.request.ProjectFormRequest;
import com.sejong.projectservice.domains.subgoal.domain.SubGoalEntity;
import com.sejong.projectservice.domains.techstack.domain.TechStackEntity;
import com.sejong.projectservice.domains.techstack.repository.TechStackRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Mapper {

    private final CategoryRepository categoryRepository;
    private final TechStackRepository techStackRepository;

    // project 생성 시에 특화된 메서드
    public void connectRelationship(ProjectEntity projectEntity, ProjectFormRequest request) {
        // cascade - collaborator
        List<CollaboratorEntity> collaboratorEntities = request.getCollaborators().stream()
                .map(collaboratorname -> CollaboratorEntity.of(collaboratorname, projectEntity))
                .toList();

        // cascade - subgoal
        List<SubGoalEntity> subGoals = request.getSubGoals().stream()
                .map(content -> SubGoalEntity.of(content, false, LocalDateTime.now(), LocalDateTime.now(),
                        projectEntity))
                .toList();

        // category 기존 것 있으면 쓰고 없으면 생성
        request.getCategories().stream()
                .map(categoryName -> categoryRepository.findByName(categoryName)
                        .orElseGet(() -> categoryRepository.save(CategoryEntity.of(categoryName))))
                .forEach(projectEntity::addCategory);

        // techstack 기존 것 있으면 쓰고 없으면 생성
        request.getTechStacks().stream()
                .map(techStackName -> techStackRepository.findByName(techStackName)
                        .orElseGet(() -> techStackRepository.save(TechStackEntity.of(techStackName))))
                .forEach(projectEntity::addTechStack);
    }
}