package com.sejong.projectservice.infrastructure.assembler;

import com.sejong.projectservice.core.project.domain.Project;
import com.sejong.projectservice.infrastructure.projectuser.entity.ProjectUserEntity;
import com.sejong.projectservice.infrastructure.project.entity.ProjectEntity;
import com.sejong.projectservice.infrastructure.projecttechstack.entity.ProjectTechStackEntity;
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

        project.getProjectUsers().forEach(
                c -> ProjectUserEntity.from(c, projectEntity)
        );

        project.getSubGoals().forEach(
                s -> SubGoalEntity.from(s, projectEntity)
        );

        project.getTechStacks().stream()
                .map(t -> techStackJpaRepository.findByName(t.getName())
                        .orElseGet(() -> techStackJpaRepository.save(new TechStackEntity(null, t.getName()))))
                .forEach(t -> ProjectTechStackEntity.from(projectEntity, t));

    }
}