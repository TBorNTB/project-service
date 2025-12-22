package com.sejong.projectservice.domains.project.repository;

import com.sejong.projectservice.domains.project.domain.ProjectDto;
import com.sejong.projectservice.support.common.error.code.ErrorCode;
import com.sejong.projectservice.support.common.error.exception.ApiException;
import com.sejong.projectservice.domains.enums.ProjectStatus;
import com.sejong.projectservice.support.common.util.Mapper;
import com.sejong.projectservice.domains.project.domain.ProjectEntity;
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
    public ProjectDto save(ProjectDto projectDto) {
        ProjectEntity projectEntity;

        if (projectDto.getId() == null) {
            projectEntity = ProjectEntity.from(projectDto);
            ProjectEntity savedProjectEntity = projectJpaRepository.save(projectEntity);

            // collaborator, subgoal, document 엔티티들이 projectentity에 종속되어있음.
            // 따라서 projectentity가 먼저 영속화 되어있어야 함.
            mapper.map(projectDto, projectEntity);
            savedProjectEntity = projectJpaRepository.save(projectEntity);

            return savedProjectEntity.toDomain();
        } else {
            projectEntity = projectJpaRepository.findById(projectDto.getId())
                    .orElseThrow(() -> new RuntimeException("project not found"));
            projectEntity.update(projectDto);
            return projectEntity.toDomain();
        }
    }

    @Override
    public Page<ProjectDto> findAll(Pageable pageable) {
        Page<ProjectEntity> pageProjectEntities = projectJpaRepository.findAll(pageable);
        List<ProjectDto> projectDtos = pageProjectEntities
                .stream()
                .map(ProjectEntity::toDomain)
                .toList();

        return new PageImpl<>(
                projectDtos,
                pageable,
                pageProjectEntities.getTotalElements()
        );
    }

    @Override
    public Page<ProjectDto> searchWithFilters(String keyword, ProjectStatus status, Pageable pageable) {
        Page<ProjectEntity> pageProjectEntities = projectJpaRepository.searchWithFilters(keyword, status,
                pageable);
        List<ProjectDto> projectDtos = pageProjectEntities.stream()
                .map(ProjectEntity::toDomain)
                .toList();
        return new PageImpl<>(
                projectDtos,
                pageable,
                pageProjectEntities.getTotalElements()
        );
    }

    @Override
    public ProjectDto findOne(Long projectId) {
        ProjectEntity projectEntity = projectJpaRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        return projectEntity.toDomain();
    }

    @Override
    public boolean existsById(Long postId) {
        return projectJpaRepository.existsById(postId);
    }

    @Override
    public ProjectDto updateCollaborator(ProjectDto projectDto) {
        ProjectEntity projectEntity = projectJpaRepository.findById(projectDto.getId())
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "해당 프로젝트트 존재하지 않습니다."));

        mapper.updateCollaborator(projectDto, projectEntity);
        return projectEntity.toDomain();
    }

    @Override
    public void deleteById(Long projectId) {
        projectJpaRepository.deleteById(projectId);
    }

    @Override
    public ProjectDto update(ProjectDto projectDto) {
        ProjectEntity projectEntity = projectJpaRepository.findById(projectDto.getId())
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "해당 사용자는 존재하지 않습니다."));

        mapper.updateCategory(projectDto, projectEntity);
        return projectEntity.toDomain();
    }

    @Override
    public Long getProjectCount() {
        return projectJpaRepository.getProjectCount();
    }

}
