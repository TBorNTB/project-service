package com.sejong.projectservice.infrastructure.project.repository;

import com.sejong.projectservice.core.enums.ProjectStatus;
import com.sejong.projectservice.core.project.domain.Project;
import com.sejong.projectservice.core.project.repository.ProjectRepository;
import com.sejong.projectservice.infrastructure.assembler.Mapper;
import com.sejong.projectservice.infrastructure.project.entity.ProjectEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProjectRepositoryImpl implements ProjectRepository {

    private final ProjectJpaRepository projectJpaRepository;
    private final Mapper mapper;

    @Override
    public Project save(Project project) {
        ProjectEntity projectEntity;

        if (project.getId() == null) {
            projectEntity = ProjectEntity.from(project);
            mapper.map(project, projectEntity);
            ProjectEntity savedProjectEntity = projectJpaRepository.save(projectEntity);
            return savedProjectEntity.toDomain();
        } else {
            projectEntity = projectJpaRepository.findById(project.getId())
                    .orElseThrow(() -> new RuntimeException("project not found"));
            projectEntity.update(project);
            return projectEntity.toDomain();
        }
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
    public Page<Project> searchWithFilters(String keyword, ProjectStatus status, Pageable pageable) {
        Page<ProjectEntity> pageProjectEntities = projectJpaRepository.searchWithFilters(keyword, status,
                pageable);
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
