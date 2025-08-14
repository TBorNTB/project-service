package com.sejong.projectservice.infrastructure.mapper;

import com.sejong.projectservice.core.project.domain.Project;
import com.sejong.projectservice.core.subgoal.SubGoal;
import com.sejong.projectservice.infrastructure.category.entity.CategoryEntity;
import com.sejong.projectservice.infrastructure.category.repository.CategoryJpaRepository;
import com.sejong.projectservice.infrastructure.collaborator.entity.CollaboratorEntity;
import com.sejong.projectservice.infrastructure.document.entity.DocumentEntity;
import com.sejong.projectservice.infrastructure.project.entity.ProjectEntity;
import com.sejong.projectservice.infrastructure.subgoal.SubGoalEntity;
import com.sejong.projectservice.infrastructure.techstack.entity.TechStackEntity;
import com.sejong.projectservice.infrastructure.techstack.repository.TechStackJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Mapper {

    private final CategoryJpaRepository categoryJpaRepository;
    private final TechStackJpaRepository techStackJpaRepository;

    // project 생성 시에 특화된 메서드
    public void map(Project project, ProjectEntity projectEntity) {

        project.getCollaborators().stream()
                .map(CollaboratorEntity::from).forEach(projectEntity::addCollaborator);

        project.getSubGoals().stream()
                .map(SubGoalEntity::from).forEach(projectEntity::addSubGoal);

        project.getDocuments().stream()
                .map(DocumentEntity::from).forEach(projectEntity::addDocument);

        project.getCategories().stream()
                .map(c -> categoryJpaRepository.findByName(c.getName())
                        .orElseGet(() -> categoryJpaRepository.save(CategoryEntity.of(c.getName()))))
                .forEach(projectEntity::addCategory);

        project.getTechStacks().stream()
                .map(t -> techStackJpaRepository.findByName(t.getName())
                        .orElseGet(() -> techStackJpaRepository.save(TechStackEntity.of(t.getName()))))
                .forEach(projectEntity::addTechStack);
    }


    public void updateCollaborator(Project project, ProjectEntity projectEntity) {
        projectEntity.getCollaborators().clear();

        project.getCollaborators().stream()
                .map(CollaboratorEntity::from).forEach(projectEntity::addCollaborator);

    //변경감지하여 영속화 하는 작업
    }
    public void updateCategory(Project project, ProjectEntity projectEntity) {
        projectEntity.getProjectCategories().clear();

        project.getCategories().stream()
                .map(c -> categoryJpaRepository.findByName(c.getName())
                        .orElseGet(() -> categoryJpaRepository.save(CategoryEntity.of(c.getName()))))
                .forEach(projectEntity::addCategory);

    }
}