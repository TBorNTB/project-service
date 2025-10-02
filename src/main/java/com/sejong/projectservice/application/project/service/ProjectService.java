package com.sejong.projectservice.application.project.service;

import com.sejong.projectservice.application.internal.UserExternalService;
import com.sejong.projectservice.application.project.assembler.Assembler;
import com.sejong.projectservice.application.project.dto.request.ProjectFormRequest;
import com.sejong.projectservice.application.project.dto.request.ProjectUpdateRequest;
import com.sejong.projectservice.application.project.dto.response.*;
import com.sejong.projectservice.core.enums.ProjectStatus;
import com.sejong.projectservice.core.project.domain.Project;
import com.sejong.projectservice.core.project.repository.ProjectRepository;
import com.sejong.projectservice.infrastructure.project.kafka.ProjectEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserExternalService userExternalService;
    private final ProjectEventPublisher projectEventPublisher;

    @Transactional
    public ProjectAddResponse createProject(ProjectFormRequest projectFormRequest, String username) {
        userExternalService.validateExistence(username, projectFormRequest.getCollaborators());
        Project project = Assembler.toProject(projectFormRequest, username);
        Project savedProject = projectRepository.save(project);
        projectEventPublisher.publishCreated(savedProject);
        return ProjectAddResponse.from(savedProject.getTitle(), "저장 완료");
    }

    @Transactional
    public ProjectUpdateResponse update(Long projectId, ProjectUpdateRequest projectUpdateRequest, String username) {
        Project project = projectRepository.findOne(projectId);
        project.validateUserPermission(username);

        project.update(projectUpdateRequest.getTitle(), projectUpdateRequest.getDescription(),
                projectUpdateRequest.getProjectStatus(),
                projectUpdateRequest.getThumbnailUrl());
        Project savedProject = projectRepository.save(project);
        projectEventPublisher.publishUpdated(savedProject);
        return ProjectUpdateResponse.from(savedProject.getTitle(), "수정 완료");
    }

    @Transactional
    public ProjectDeleteResponse removeProject(String username, Long projectId, String userRole) {
        Project project = projectRepository.findOne(projectId);
        project.validateOwner(username, userRole);
        projectRepository.deleteById(projectId);
        projectEventPublisher.publishDeleted(projectId.toString());
        return ProjectDeleteResponse.of(project.getTitle(), "삭제 완료");
    }

    @Transactional(readOnly = true)
    public ProjectPageResponse getAllProjects(Pageable pageable) {

        Page<Project> projectPage = projectRepository.findAll(pageable);
        List<String> usernames = ProjectUsernamesExtractor.extract(projectPage.getContent());

        Map<String, String> usernamesMap = userExternalService.getAllUsernames(usernames);
        return ProjectPageResponse.from(projectPage, usernamesMap);
    }

    @Transactional(readOnly = true)
    public ProjectPageResponse search(String keyword, ProjectStatus status, Pageable pageable) {
        Page<Project> projectPage = projectRepository.searchWithFilters(keyword, status, pageable);
        List<String> usernames = ProjectUsernamesExtractor.extract(projectPage.getContent());
        Map<String, String> usernamesMap = userExternalService.getAllUsernames(usernames);
        return ProjectPageResponse.from(projectPage, usernamesMap);
    }

    @Transactional(readOnly = true)
    public ProjectSpecifyInfo findOne(Long projectId) {
        Project project = projectRepository.findOne(projectId);
        List<String> usernames = ProjectUsernamesExtractor.extract(project);
        Map<String, String> usernamesMap = userExternalService.getAllUsernames(usernames);
        return ProjectSpecifyInfo.from(project, usernamesMap);
    }

    @Transactional(readOnly = true)
    public boolean exists(Long postId) {
        return projectRepository.existsById(postId);
    }
}
