package com.sejong.projectservice.application.project.controller;

import static com.sejong.projectservice.application.project.controller.fixture.ProjectFixture.createProject;
import static com.sejong.projectservice.application.project.controller.fixture.ProjectFixture.createProjectAddResponse;
import static com.sejong.projectservice.application.project.controller.fixture.ProjectFixture.createProjectFormRequest;
import static com.sejong.projectservice.application.project.controller.fixture.ProjectFixture.createProjectPageResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sejong.projectservice.application.project.config.MockBeansConfig;
import com.sejong.projectservice.domains.project.dto.request.ProjectFormRequest;
import com.sejong.projectservice.domains.project.dto.request.ProjectUpdateRequest;
import com.sejong.projectservice.domains.project.dto.response.ProjectAddResponse;
import com.sejong.projectservice.domains.project.dto.response.ProjectPageResponse;
import com.sejong.projectservice.domains.project.dto.response.ProjectSpecifyInfo;
import com.sejong.projectservice.domains.project.dto.response.ProjectUpdateResponse;
import com.sejong.projectservice.domains.project.service.ProjectService;
import com.sejong.projectservice.support.common.constants.ProjectStatus;
import com.sejong.projectservice.domains.project.domain.ProjectDto;
import com.sejong.projectservice.domains.project.controller.ProjectController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@WebMvcTest(ProjectController.class)
@AutoConfigureMockMvc
@Import(MockBeansConfig.class)
class ProjectDtoControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ProjectService projectService;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void 헬스체크는_정상적으로_작동한다() throws Exception {
        //when && then
        mockMvc.perform(get("/api/project/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }

    @Test
    void 프로젝트를_정상적으로_저장한다() throws Exception {
        //given
        ProjectFormRequest projectFormRequest = createProjectFormRequest("테스트제목");
        ProjectAddResponse response = createProjectAddResponse("테스트제목");
//        when(projectService.createProject(projectFormRequest,1L)).thenReturn(response);

        //when && then
        mockMvc.perform(post("/api/project")
                        .header("X-User-ID", "123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectFormRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("테스트제목"));
    }

    @GetMapping("/all")
    public ResponseEntity<ProjectPageResponse> getAll(
            @RequestParam(name = "size") int size,
            @RequestParam(name = "page") int page
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        ProjectPageResponse response = projectService.getAllProjects(pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @Test
    void 프로젝트_리스트를_페이지네이션_적용하여_반환한다() throws Exception {
        //given
        int page = 0;
        int size = 10;
        ProjectPageResponse response = createProjectPageResponse();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        when(projectService.getAllProjects(pageable)).thenReturn(response);
        //when && then
        mockMvc.perform(get("/api/project")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projects[0].title").value("테스트 프로젝트 A"));
    }

    @Test
    void 변경된_프로젝트를_정상적으로_갱신한다() throws Exception {
        //given
        ProjectUpdateRequest request = ProjectUpdateRequest.builder()
                .title("변경된_제목")
                .build();

        ProjectUpdateResponse response = ProjectUpdateResponse.from("변경된_제목", "변경이 완료되었습니다.");
        Long projectId = 1L;

        when(projectService.update(projectId, request)).thenReturn(response);

        //when && then
        mockMvc.perform(put("/api/project/" + projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("변경된_제목"));
    }

    @Test
    void 특정_프로젝트를_정상적으로_반환한다() throws Exception {
        //given
        Long projectId = 1L;
        ProjectDto projectDto = createProject("테스트_제목");
        ProjectSpecifyInfo response = ProjectSpecifyInfo.from(projectDto);
        when(projectService.findOne(projectId)).thenReturn(response);

        //when && then
        mockMvc.perform(get("/api/project/" + projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("테스트_제목"));
    }

    @Test
    void 검색조건에_따라_프로젝트를_정상적으로_조회한다() throws Exception {
        // given
        String keyword = "테스트";
        String status = "IN_PROGRESS"; // enum name
        String sort = "createdAt";
        String direction = "desc";
        int page = 1;
        int size = 10;

        ProjectPageResponse response = createProjectPageResponse();

        when(projectService.search(
                eq(keyword),
                eq(ProjectStatus.valueOf(status)),
                any(Pageable.class))
        ).thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/project/search")
                        .param("keyword", keyword)
                        .param("status", status)
                        .param("sort", sort)
                        .param("direction", direction)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projects[0].title").value("테스트 프로젝트 A"))
                .andExpect(jsonPath("$.size").value(size))
                .andExpect(jsonPath("$.page").value(page))
                .andExpect(jsonPath("$.totalElements").value(2));
    }
}