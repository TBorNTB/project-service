package com.sejong.projectservice.application.project.service;

import com.sejong.projectservice.application.project.assembler.Assembler;
import com.sejong.projectservice.application.project.dto.request.ProjectFormRequest;
import com.sejong.projectservice.application.project.dto.request.ProjectUpdateRequest;
import com.sejong.projectservice.application.project.dto.response.ProjectAddResponse;
import com.sejong.projectservice.application.project.dto.response.ProjectPageResponse;
import com.sejong.projectservice.application.project.dto.response.ProjectSpecifyInfo;
import com.sejong.projectservice.application.project.dto.response.ProjectUpdateResponse;
import com.sejong.projectservice.core.enums.ProjectStatus;
import com.sejong.projectservice.core.project.domain.Project;
import com.sejong.projectservice.core.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
//    private final UserClient userClient;

    @Transactional
    public ProjectAddResponse createProject(ProjectFormRequest projectFormRequest) {
        Project project = Assembler.toProject(projectFormRequest);
        Project savedProject = projectRepository.save(project);
        return ProjectAddResponse.from(savedProject.getTitle(), "저장 완료");
    }

    public ProjectPageResponse getAllProjects(Pageable pageable) {

        Page<Project> projectPage = projectRepository.findAll(pageable);
        return ProjectPageResponse.from(projectPage);
    }

    @Transactional
    public ProjectUpdateResponse update(Long projectId, ProjectUpdateRequest projectUpdateRequest) {
        Project project = projectRepository.findOne(projectId);
        project.update(projectUpdateRequest.getTitle(), projectUpdateRequest.getDescription(),
                projectUpdateRequest.getProjectStatus(),
                projectUpdateRequest.getThumbnailUrl());
        Project savedProject = projectRepository.save(project);
        return ProjectUpdateResponse.from(savedProject.getTitle(), "수정 완료");
    }

    public ProjectPageResponse search(String keyword, ProjectStatus status, Pageable pageable) {
        Page<Project> projectPage = projectRepository.searchWithFilters(keyword, status, pageable);
        return ProjectPageResponse.from(projectPage);
    }

    public ProjectSpecifyInfo findOne(Long projectId) {
        Project project = projectRepository.findOne(projectId);
        return ProjectSpecifyInfo.from(project);
    }

    @Transactional(readOnly = true)
    public boolean exists(Long postId) {
        return projectRepository.existsById(postId);
    }
}
