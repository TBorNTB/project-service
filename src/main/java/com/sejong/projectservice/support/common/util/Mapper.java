package com.sejong.projectservice.support.common.util;

import com.sejong.projectservice.domains.project.dto.ProjectDto;
import com.sejong.projectservice.domains.category.domain.CategoryEntity;
import com.sejong.projectservice.domains.category.repository.CategoryRepository;
import com.sejong.projectservice.domains.collaborator.domain.CollaboratorEntity;
import com.sejong.projectservice.domains.document.domain.DocumentEntity;
import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import com.sejong.projectservice.domains.project.dto.request.ProjectFormRequest;
import com.sejong.projectservice.domains.subgoal.domain.SubGoalEntity;
import com.sejong.projectservice.domains.techstack.domain.TechStackEntity;
import com.sejong.projectservice.domains.techstack.repository.TechStackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Mapper {

    private final CategoryRepository categoryRepository;
    private final TechStackRepository techStackRepository;

    // project 생성 시에 특화된 메서드
    public void map(ProjectDto projectDto, ProjectEntity projectEntity) {

        projectDto.getCollaboratorDtos().stream()
                .map(CollaboratorEntity::from).forEach(projectEntity::addCollaborator);

        projectDto.getSubGoalDtos().stream()
                .map(SubGoalEntity::from).forEach(projectEntity::addSubGoal);

        projectDto.getDocumentDtos().stream()
                .map(DocumentEntity::from).forEach(projectEntity::addDocument);

        projectDto.getCategories().stream()
                .map(c -> categoryRepository.findByName(c.getName())
                        .orElseGet(() -> categoryRepository.save(CategoryEntity.of(c.getName()))))
                .forEach(projectEntity::addCategory);

        projectDto.getTechStackDtos().stream()
                .map(t -> techStackRepository.findByName(t.getName())
                        .orElseGet(() -> techStackRepository.save(TechStackEntity.of(t.getName()))))
                .forEach(projectEntity::addTechStack);
    }

    public void connectJoins(ProjectEntity projectEntity, ProjectFormRequest request) {

        List<CollaboratorEntity> collaboratorEntities = request.getCollaborators().stream()
                .map(collaboratorname -> CollaboratorEntity.of(collaboratorname, projectEntity))
                .toList();

        List<SubGoalEntity> subGoals = request.getSubGoals().stream()
                .map(content -> SubGoalEntity.of(content, false, LocalDateTime.now(), LocalDateTime.now(), projectEntity))
                .toList();

        request.getCategories().stream()
                .map(categoryName -> categoryRepository.findByName(categoryName)
                        .orElseGet(() -> categoryRepository.save(CategoryEntity.of(categoryName))))
                .forEach(projectEntity::addCategory);

        request.getTechStacks().stream()
                .map(techStackName -> techStackRepository.findByName(techStackName)
                        .orElseGet(() -> techStackRepository.save(TechStackEntity.of(techStackName))))
                .forEach(projectEntity::addTechStack);
    }


    public void updateCollaborator(ProjectDto projectDto, ProjectEntity projectEntity) {
        projectEntity.getCollaboratorEntities().clear();

        projectDto.getCollaboratorDtos().stream()
                .map(CollaboratorEntity::from).forEach(projectEntity::addCollaborator);

    //변경감지하여 영속화 하는 작업
    }
    public void updateCategory(ProjectDto projectDto, ProjectEntity projectEntity) {
        projectEntity.getProjectCategories().clear();

        projectDto.getCategories().stream()
                .map(c -> categoryRepository.findByName(c.getName())
                        .orElseGet(() -> categoryRepository.save(CategoryEntity.of(c.getName()))))
                .forEach(projectEntity::addCategory);

    }
}