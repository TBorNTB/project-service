package com.sejong.projectservice.application.project.service;

import static com.sejong.projectservice.application.project.controller.fixture.ProjectFixture.createProject;
import static com.sejong.projectservice.application.project.controller.fixture.ProjectFixture.createProjectUpdateRequest;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sejong.projectservice.application.project.dto.request.ProjectFormRequest;
import com.sejong.projectservice.application.project.dto.request.ProjectUpdateRequest;
import com.sejong.projectservice.application.project.dto.response.ProjectAddResponse;
import com.sejong.projectservice.application.project.dto.response.ProjectPageResponse;
import com.sejong.projectservice.application.project.dto.response.ProjectSpecifyInfo;
import com.sejong.projectservice.application.project.dto.response.ProjectUpdateResponse;
import com.sejong.projectservice.core.document.repository.DocumentRepository;
import com.sejong.projectservice.core.enums.ProjectStatus;
import com.sejong.projectservice.core.project.domain.Project;
import com.sejong.projectservice.core.project.repository.ProjectRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @Mock
    ProjectRepository projectRepository;

    @Mock
    DocumentRepository documentRepository;

    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        projectService = new ProjectService(projectRepository);
    }

    @Test
    void 프로젝트를_정상적으로_저장한다() {
        // given
        ProjectFormRequest mockRequest = mock(ProjectFormRequest.class);
        String userId = "1";
        Project project = createProject("테스트제목");
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        // when
        ProjectAddResponse response = projectService.createProject(mockRequest);

        // then
        assertThat(response.getTitle()).isEqualTo("테스트제목");

    }

    @Test
    void 모든_프로젝트를_반환한다() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Project project1 = createProject("테스트제목1");
        Project project2 = createProject("테스트제목2");
        Page<Project> projectPage = new PageImpl<>(List.of(project1, project2), pageable, 2);

        when(projectRepository.findAll(pageable)).thenReturn(projectPage);
        // when
        ProjectPageResponse response = projectService.getAllProjects(pageable);

        // then
        assertThat(response.getProjects().get(0).getTitle()).isEqualTo("테스트제목1");
        assertThat(response.getProjects().get(1).getTitle()).isEqualTo("테스트제목2");
        assertThat(response.getSize()).isEqualTo(10);
        assertThat(response.getTotalElements()).isEqualTo(2);
    }

    @Test
    void 프로젝트를_정상적으로_갱신한다() {
        // given
        Long projectId = 1L;
        Project project = createProject("테스트제목1");

        ProjectUpdateRequest request = createProjectUpdateRequest("변경된 제목");

        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectRepository.findOne(projectId)).thenReturn(project);

        // when
        ProjectUpdateResponse response = projectService.update(projectId, request);

        // then
        assertThat(response.getTitle()).isEqualTo("변경된 제목");
    }

    @Test
    void 필터에_맞게_프로젝트를_찾는다() {
        // given
        String keyword = "keyword";
        ProjectStatus status = ProjectStatus.IN_PROGRESS;
        Pageable pageable = PageRequest.of(0, 10);
        Project project1 = createProject("테스트제목1");
        Project project2 = createProject("테스트제목2");
        List<Project> projects = List.of(project1, project2);
        Page<Project> projectPage = new PageImpl<>(projects, pageable, 2);
        when(projectRepository.searchWithFilters(keyword, status, pageable)).thenReturn(projectPage);

        // when
        ProjectPageResponse response = projectService.search(keyword, status, pageable);

        // then
        assertThat(response.getProjects().get(0).getTitle()).isEqualTo("테스트제목1");
        assertThat(response.getProjects().get(1).getTitle()).isEqualTo("테스트제목2");
        assertThat(response.getSize()).isEqualTo(10);
        assertThat(response.getTotalElements()).isEqualTo(2);
    }

    @Test
    void 특정_프로젝트를_조회한다() {
        // given
        Long projectId = 1L;
        Project project = createProject("테스트제목1");
        when(projectRepository.findOne(projectId)).thenReturn(project);

        // when
        ProjectSpecifyInfo response = projectService.findOne(projectId);

        // then
        assertThat(response.getTitle()).isEqualTo("테스트제목1");
    }

}