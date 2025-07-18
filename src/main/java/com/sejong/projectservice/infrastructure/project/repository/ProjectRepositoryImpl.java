package com.sejong.projectservice.infrastructure.project.repository;

import com.sejong.projectservice.core.assembler.ProjectAssembler;
import com.sejong.projectservice.core.enums.Category;
import com.sejong.projectservice.core.enums.ProjectStatus;
import com.sejong.projectservice.core.project.domain.Project;
import com.sejong.projectservice.core.project.repository.ProjectRepository;
import com.sejong.projectservice.infrastructure.assembler.ProjectEntityAssembler;
import com.sejong.projectservice.infrastructure.collborator.entity.CollaboratorEntity;
import com.sejong.projectservice.infrastructure.project.entity.ProjectEntity;
import com.sejong.projectservice.infrastructure.projecttechstack.entity.ProjectTechStackEntity;
import com.sejong.projectservice.infrastructure.subgoal.SubGoalEntity;
import com.sejong.projectservice.infrastructure.techstack.entity.TechStackEntity;
import com.sejong.projectservice.infrastructure.techstack.repository.TechStackJpaRepository;
import com.sejong.projectservice.infrastructure.techstack.repository.TechStackRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProjectRepositoryImpl implements ProjectRepository {

    private final ProjectJpaRepository projectJpaRepository;
    private final ProjectEntityAssembler projectAssembler;

    @Override
    public Project save(Project project) {

        ProjectEntity projectEntity = ProjectEntity.from(project);
        projectAssembler.assemble(projectEntity, project);
        ProjectEntity entity = projectJpaRepository.save(projectEntity);
        return entity.toDomain();
    }

    @Override
    public Page<Project> findAll(Pageable pageable) {
        Page<ProjectEntity> pageProjectEntities = projectJpaRepository.findAll(pageable);
        List<Project> projects = pageProjectEntities
                .stream()
                .map(ProjectEntity::toDomain)
                .toList();

        return new PageImpl<>(
                projects,
                pageable,
                pageProjectEntities.getTotalElements()
        );
    }

    @Override
    @Transactional
    public Project update(Project project, Long projectId) {
        ProjectEntity projectEntity = projectJpaRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        projectEntity.updateBasicInfo(project);
        projectEntity.clearAllRelations();
        projectAssembler.assemble(projectEntity, project);

        ProjectEntity responseEntity = projectJpaRepository.save(projectEntity);
        return responseEntity.toDomain();
    }

    @Override
    public Page<Project> searchWithFilters(String keyword, Category category, ProjectStatus status, Pageable pageable) {
        Page<ProjectEntity> pageProjectEntities = projectJpaRepository.searchWithFilters(keyword, category, status, pageable);
        List<Project> projects = pageProjectEntities.stream()
                .map(ProjectEntity::toDomain)
                .toList();
        return new PageImpl<>(
                projects,
                pageable,
                pageProjectEntities.getTotalElements()
        );
    }

    @Override
    public Project findOne(Long projectId) {
        ProjectEntity projectEntity = projectJpaRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        return projectEntity.toDomain();
    }

}
