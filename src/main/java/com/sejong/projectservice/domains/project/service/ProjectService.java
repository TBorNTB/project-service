package com.sejong.projectservice.domains.project.service;

import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import com.sejong.projectservice.domains.project.kafka.dto.ProjectCreatedEventDto;
import com.sejong.projectservice.domains.project.kafka.dto.ProjectDeletedEventDto;
import com.sejong.projectservice.domains.project.kafka.dto.ProjectUpdatedEventDto;
import com.sejong.projectservice.domains.project.repository.ProjectRepository;
import com.sejong.projectservice.domains.project.dto.request.ProjectFormRequest;
import com.sejong.projectservice.domains.project.dto.request.ProjectUpdateRequest;
import com.sejong.projectservice.domains.project.dto.response.ProjectAddResponse;
import com.sejong.projectservice.domains.project.dto.response.ProjectDeleteResponse;
import com.sejong.projectservice.domains.project.dto.response.ProjectPageResponse;
import com.sejong.projectservice.domains.project.dto.response.ProjectSpecifyInfo;
import com.sejong.projectservice.domains.project.dto.response.ProjectUpdateResponse;
import com.sejong.projectservice.domains.project.util.ProjectUsernamesExtractor;
import com.sejong.projectservice.support.common.internal.UserExternalService;
import com.sejong.projectservice.support.common.internal.response.PostLikeCheckResponse;
import com.sejong.projectservice.support.common.internal.response.UserNameInfo;
import com.sejong.projectservice.support.common.constants.ProjectStatus;
import com.sejong.projectservice.domains.project.kafka.ProjectEventPublisher;
import java.util.List;
import java.util.Map;

import com.sejong.projectservice.support.common.exception.BaseException;
import com.sejong.projectservice.support.common.exception.ExceptionType;
import com.sejong.projectservice.support.common.util.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final UserExternalService userExternalService;
    private final ProjectEventPublisher projectEventPublisher;
    private final ProjectRepository projectRepository;
    private final Mapper mapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public ProjectAddResponse createProject(ProjectFormRequest projectFormRequest, String username) {
        userExternalService.validateExistence(username, projectFormRequest.getCollaborators());
        Map<String, UserNameInfo> userNameInfos = userExternalService.getUserNameInfos(List.of(username));
        ProjectEntity projectEntity = ProjectEntity.of(projectFormRequest,username,userNameInfos.get(username));
        ProjectEntity savedProject = projectRepository.save(projectEntity);
        mapper.connectJoins(savedProject,projectFormRequest);
        eventPublisher.publishEvent(ProjectCreatedEventDto.of(savedProject.getId()));
        return ProjectAddResponse.from(savedProject.getTitle(), "저장 완료");
    }

    @Transactional
    public ProjectUpdateResponse update(Long projectId, ProjectUpdateRequest projectUpdateRequest, String username) {
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException(ExceptionType.BAD_REQUEST));
        project.validateUserPermission(username);

        project.update(projectUpdateRequest.getTitle(), projectUpdateRequest.getDescription(),
                projectUpdateRequest.getProjectStatus(),
                projectUpdateRequest.getThumbnailUrl());
        ProjectEntity savedProject = projectRepository.save(project);
        eventPublisher.publishEvent(ProjectUpdatedEventDto.of(savedProject.getId()));
        return ProjectUpdateResponse.from(savedProject.getTitle(), "수정 완료");
    }

    @Transactional
    public ProjectDeleteResponse removeProject(String username, Long projectId, String userRole) {
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException(ExceptionType.BAD_REQUEST));
        project.validateOwner(username, userRole);
        projectRepository.deleteById(projectId);
        eventPublisher.publishEvent(ProjectDeletedEventDto.of(projectId));
        return ProjectDeleteResponse.of(project.getTitle(), "삭제 완료");
    }

    @Transactional(readOnly = true)
    public ProjectPageResponse getAllProjects(Pageable pageable) {

        Page<ProjectEntity> projectEntityPage = projectRepository.findAll(pageable);
        List<String> usernames = ProjectUsernamesExtractor.extract(projectEntityPage.getContent());

        Map<String, UserNameInfo> usernamesMap = userExternalService.getUserNameInfos(usernames);
        return ProjectPageResponse.from(projectEntityPage, usernamesMap);
    }

    @Transactional(readOnly = true)
    public ProjectPageResponse search(String keyword, ProjectStatus status, Pageable pageable) {
        Page<ProjectEntity> projectEntitiesPage = projectRepository.searchWithFilters(keyword, status, pageable);
        List<String> usernames = ProjectUsernamesExtractor.extract(projectEntitiesPage.getContent());

        Map<String, UserNameInfo> usernamesMap = userExternalService.getUserNameInfos(usernames);
        return ProjectPageResponse.from(projectEntitiesPage, usernamesMap);
    }

    @Transactional(readOnly = true)
    public ProjectSpecifyInfo findOne(Long projectId) {
        ProjectEntity projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException(ExceptionType.BAD_REQUEST));

        List<String> usernames = ProjectUsernamesExtractor.extract(projectEntity);

        Map<String, UserNameInfo> usernamesMap = userExternalService.getUserNameInfos(usernames);
        return ProjectSpecifyInfo.from(projectEntity, usernamesMap);
    }

    @Transactional(readOnly = true)
    public PostLikeCheckResponse checkPost(Long postId) {
        boolean exists = projectRepository.existsById(postId);
        if (exists) {
            ProjectEntity project  = projectRepository.findById(postId)
                    .orElseThrow(() -> new BaseException(ExceptionType.BAD_REQUEST));
            return PostLikeCheckResponse.hasOfProject(project, true);
        }

        return PostLikeCheckResponse.hasNotOf();
    }

    @Transactional(readOnly = true)
    public boolean exists(Long postId) {
        return projectRepository.existsById(postId);
    }

    @Transactional(readOnly = true)
    public Long getProjectCount() {
        Long count = projectRepository.getProjectCount();
        return count;
    }
}
