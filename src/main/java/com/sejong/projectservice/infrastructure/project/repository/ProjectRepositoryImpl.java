package com.sejong.projectservice.infrastructure.project.repository;

import com.sejong.projectservice.core.common.PageResult;
import com.sejong.projectservice.core.common.PageSearchCommand;
import com.sejong.projectservice.core.enums.Category;
import com.sejong.projectservice.core.enums.ProjectStatus;
import com.sejong.projectservice.core.project.domain.Project;
import com.sejong.projectservice.core.project.repository.ProjectRepository;
import com.sejong.projectservice.infrastructure.assembler.ProjectEntityAssembler;
import com.sejong.projectservice.infrastructure.project.entity.ProjectEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public PageResult<Project> findAll(PageSearchCommand pageSearchCommand) {
        Pageable pageable = PageRequest.of(
                pageSearchCommand.getPage(),
                pageSearchCommand.getSize(),
                Sort.by(Sort.Direction.valueOf(pageSearchCommand.getDirection().toUpperCase()), pageSearchCommand.getSort())
        );
        Page<ProjectEntity> pageProjectEntities = projectJpaRepository.findAll(pageable);
        List<Project> projects = pageProjectEntities
                .stream()
                .map(ProjectEntity::toDomain)
                .toList();

        return PageResult.from(projects,pageProjectEntities.getSize(),pageProjectEntities.getNumber(),pageProjectEntities.getTotalPages(),pageProjectEntities.getTotalElements());
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
    public PageResult<Project> searchWithFilters(String keyword, Category category, ProjectStatus status, PageSearchCommand pageSearchCommand) {
        Pageable pageable = PageRequest.of(
                pageSearchCommand.getPage(),
                pageSearchCommand.getSize(),
                Sort.by(Sort.Direction.valueOf(pageSearchCommand.getDirection().toUpperCase()), pageSearchCommand.getSort())
        );

        Page<ProjectEntity> pageProjectEntities = projectJpaRepository.searchWithFilters(keyword, category, status, pageable);
        List<Project> projects = pageProjectEntities.stream()
                .map(ProjectEntity::toDomain)
                .toList();
        return PageResult.from(projects,pageProjectEntities.getSize(),pageProjectEntities.getNumber(),pageProjectEntities.getTotalPages(),pageProjectEntities.getTotalElements());
    }

    @Override
    public Project findOne(Long projectId) {
        ProjectEntity projectEntity = projectJpaRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        return projectEntity.toDomain();
    }

}
