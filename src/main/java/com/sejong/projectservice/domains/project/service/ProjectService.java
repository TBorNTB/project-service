package com.sejong.projectservice.domains.project.service;

import com.sejong.projectservice.domains.project.util.Assembler;
import com.sejong.projectservice.domains.project.dto.request.ProjectFormRequest;
import com.sejong.projectservice.domains.project.dto.request.ProjectUpdateRequest;
import com.sejong.projectservice.domains.project.dto.response.ProjectAddResponse;
import com.sejong.projectservice.domains.project.dto.response.ProjectDeleteResponse;
import com.sejong.projectservice.domains.project.dto.response.ProjectPageResponse;
import com.sejong.projectservice.domains.project.dto.response.ProjectSpecifyInfo;
import com.sejong.projectservice.domains.project.dto.response.ProjectUpdateResponse;
import com.sejong.projectservice.domains.project.util.ProjectUsernamesExtractor;
import com.sejong.projectservice.client.UserExternalService;
import com.sejong.projectservice.client.response.PostLikeCheckResponse;
import com.sejong.projectservice.client.response.UserNameInfo;
import com.sejong.projectservice.domains.enums.ProjectStatus;
import com.sejong.projectservice.domains.project.domain.Project;
import com.sejong.projectservice.domains.project.repository.ProjectRepository;
import com.sejong.projectservice.domains.project.kafka.ProjectEventPublisher;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserExternalService userExternalService;
    private final ProjectEventPublisher projectEventPublisher;

    @Transactional
    public ProjectAddResponse createProject(ProjectFormRequest projectFormRequest, String username) {
        userExternalService.validateExistence(username, projectFormRequest.getCollaborators());
        Map<String, UserNameInfo> userNameInfos = userExternalService.getUserNameInfos(List.of(username));
        Project project = Assembler.
                toProject(projectFormRequest, username, userNameInfos.get(username));
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

        Map<String, UserNameInfo> usernamesMap = userExternalService.getUserNameInfos(usernames);
        return ProjectPageResponse.from(projectPage, usernamesMap);
    }

    @Transactional(readOnly = true)
    public ProjectPageResponse search(String keyword, ProjectStatus status, Pageable pageable) {
        Page<Project> projectPage = projectRepository.searchWithFilters(keyword, status, pageable);
        List<String> usernames = ProjectUsernamesExtractor.extract(projectPage.getContent());

        Map<String, UserNameInfo> usernamesMap = userExternalService.getUserNameInfos(usernames);
        return ProjectPageResponse.from(projectPage, usernamesMap);
    }

    @Transactional(readOnly = true)
    public ProjectSpecifyInfo findOne(Long projectId) {
        Project project = projectRepository.findOne(projectId);
        List<String> usernames = ProjectUsernamesExtractor.extract(project);

        Map<String, UserNameInfo> usernamesMap = userExternalService.getUserNameInfos(usernames);
        return ProjectSpecifyInfo.from(project, usernamesMap);
    }

    @Transactional(readOnly = true)
    public PostLikeCheckResponse checkPost(Long postId) {
        boolean exists = projectRepository.existsById(postId);
        if (exists) {
            Project project = projectRepository.findOne(postId);
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
