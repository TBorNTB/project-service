package com.sejong.projectservice.support.common.util;

import com.sejong.projectservice.domains.project.domain.Project;
import com.sejong.projectservice.domains.category.domain.CategoryEntity;
import com.sejong.projectservice.domains.category.repository.CategoryJpaRepository;
import com.sejong.projectservice.domains.collaborator.domain.CollaboratorEntity;
import com.sejong.projectservice.domains.document.domain.DocumentEntity;
import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import com.sejong.projectservice.domains.subgoal.domain.SubGoalEntity;
import com.sejong.projectservice.domains.techstack.domain.TechStackEntity;
import com.sejong.projectservice.domains.techstack.repository.TechStackJpaRepository;
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