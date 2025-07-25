package com.sejong.projectservice.infrastructure.assembler;

import com.sejong.projectservice.core.project.domain.Project;
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
public class ProjectEntityAssembler {

    private final TechStackJpaRepository techStackJpaRepository;

    public void assemble(ProjectEntity projectEntity, Project project) {

        project.getCollaborators().stream()
                .map(CollaboratorEntity::from).forEach(projectEntity::addCollaborator);

        project.getSubGoals().stream()
                .map(SubGoalEntity::from).forEach(projectEntity::addSubGoal);

        project.getTechStacks().stream()
                .map(t -> techStackJpaRepository.findByName(t.getName())
                        .orElseGet(() -> techStackJpaRepository.save(TechStackEntity.of(t.getName()))))
                .forEach(projectEntity::addTechStack);

        project.getDocuments().stream()
                .map(DocumentEntity::from).forEach(projectEntity::addDocument);
    }
}