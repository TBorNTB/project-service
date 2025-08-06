package com.sejong.projectservice.infrastructure.project.repository;

import com.sejong.projectservice.application.common.error.code.ErrorCode;
import com.sejong.projectservice.application.common.error.exception.ApiException;
import com.sejong.projectservice.core.enums.ProjectStatus;
import com.sejong.projectservice.core.project.domain.Project;
import com.sejong.projectservice.core.project.repository.ProjectRepository;
import com.sejong.projectservice.infrastructure.mapper.Mapper;
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
            ProjectEntity savedProjectEntity = projectJpaRepository.save(projectEntity);

            // collaborator, subgoal, document 엔티티들이 projectentity에 종속되어있음.
            // 따라서 projectentity가 먼저 영속화 되어있어야 함.
            mapper.map(project, projectEntity);
            savedProjectEntity = projectJpaRepository.save(projectEntity);

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

    @Override
    public boolean existsById(Long postId) {
        return projectJpaRepository.existsById(postId);
    }

    @Override
    public Project updateCollaborator(Project project) {
        ProjectEntity projectEntity = projectJpaRepository.findById(project.getId())
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "해당 프로젝트트 존재하지 않습니다."));

        mapper.updateCollaborator(project, projectEntity);
        return projectEntity.toDomain();
    }

    @Override
    public void deleteById(Long projectId) {
        projectJpaRepository.deleteById(projectId);
    }

}
