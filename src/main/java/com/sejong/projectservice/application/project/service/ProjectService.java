package com.sejong.projectservice.application.project.service;

import com.sejong.projectservice.application.project.dto.request.ProjectFormRequest;
import com.sejong.projectservice.application.project.dto.response.ProjectAddResponse;
import com.sejong.projectservice.application.project.dto.response.ProjectPageResponse;
import com.sejong.projectservice.application.project.dto.response.ProjectSpecifyInfo;
import com.sejong.projectservice.application.project.dto.response.ProjectUpdateResponse;
import com.sejong.projectservice.core.assembler.ProjectAssembler;
import com.sejong.projectservice.core.enums.Category;
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
    public ProjectAddResponse register(ProjectFormRequest projectFormRequest, String userId) {
//        try {
//            boolean exists = userClient.exists(userId);
//            if (!exists) {
//                userId = "1";
//            }
//        } catch (Exception e) {
//
//            System.err.println("user-service 호출 실패: " + e.getMessage());
//            userId = "1";
//        }
        userId = "1";

        Project project = ProjectAssembler.toDomain(projectFormRequest , Long.valueOf(userId));
        Project savedProject = projectRepository.save(project);
        return ProjectAddResponse.from(savedProject.getTitle(), "저장 완료");
    }

    public ProjectPageResponse getAllProjects(Pageable pageable) {

        Page<Project> projectPage = projectRepository.findAll(pageable);
        return ProjectPageResponse.from(projectPage);
    }

    public ProjectUpdateResponse update(Long projectId, ProjectFormRequest projectFormRequest) {
        Project project = ProjectAssembler.toDomain(projectFormRequest, null);
        Project updatedProject = projectRepository.update(project , projectId);
        return ProjectUpdateResponse.from(updatedProject.getTitle(), "수정 완료");
    }

    public ProjectPageResponse search(String keyword, Category category, ProjectStatus status, Pageable pageable) {
        Page<Project> projectPage = projectRepository.searchWithFilters(keyword, category, status, pageable);
        return ProjectPageResponse.from(projectPage);
    }

    public ProjectSpecifyInfo findOne(Long projectId) {
        Project project = projectRepository.findOne(projectId);
        return ProjectSpecifyInfo.from(project);
    }
}
