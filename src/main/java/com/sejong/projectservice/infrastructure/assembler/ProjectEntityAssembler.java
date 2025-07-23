package com.sejong.projectservice.infrastructure.assembler;

import com.sejong.projectservice.core.project.domain.Project;
import com.sejong.projectservice.infrastructure.collborator.entity.CollaboratorEntity;
import com.sejong.projectservice.infrastructure.project.entity.ProjectEntity;
import com.sejong.projectservice.infrastructure.projecttechstack.entity.ProjectTechStackEntity;
import com.sejong.projectservice.infrastructure.subgoal.SubGoalEntity;
import com.sejong.projectservice.infrastructure.techstack.entity.TechStackEntity;
import com.sejong.projectservice.infrastructure.techstack.repository.TechStackJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectEntityAssembler {

    private final TechStackJpaRepository techStackJpaRepository;

    public void assemble(ProjectEntity projectEntity, Project project) {
        List<CollaboratorEntity> collaborators = projectEntity.getCollaborators();
        project.getCollaborators().forEach(
                c -> collaborators.add(CollaboratorEntity.from(c, projectEntity))
        );

        List<SubGoalEntity> subGoals = projectEntity.getSubGoals();
        project.getSubGoals().forEach(
                s -> subGoals.add(SubGoalEntity.from(s, projectEntity))
        );

        List<ProjectTechStackEntity> projectTechStacks = projectEntity.getProjectTechStacks();
        project.getTechStacks().stream()
                .map(t -> techStackJpaRepository.findByName(t.getName())
                        .orElseGet(() -> techStackJpaRepository.save(new TechStackEntity(null, t.getName()))))
                .forEach(t -> projectTechStacks.add(ProjectTechStackEntity.from(projectEntity, t)));

    }
}