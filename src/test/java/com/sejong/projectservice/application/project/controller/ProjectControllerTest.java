package com.sejong.projectservice.application.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sejong.projectservice.application.project.config.MockBeansConfig;
import com.sejong.projectservice.application.project.controller.fixture.ProjectFixture;
import com.sejong.projectservice.application.project.dto.request.ProjectFormRequest;
import com.sejong.projectservice.application.project.dto.response.ProjectAddResponse;
import com.sejong.projectservice.application.project.dto.response.ProjectPageResponse;
import com.sejong.projectservice.application.project.dto.response.ProjectSpecifyInfo;
import com.sejong.projectservice.application.project.dto.response.ProjectUpdateResponse;
import com.sejong.projectservice.application.project.service.ProjectService;
import com.sejong.projectservice.core.enums.Category;
import com.sejong.projectservice.core.enums.ProjectStatus;
import com.sejong.projectservice.core.project.domain.Project;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sejong.projectservice.application.project.controller.fixture.ProjectFixture.*;
import static com.sejong.projectservice.application.project.controller.fixture.ProjectFixture.createProjectFormRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ProjectController.class)
@AutoConfigureMockMvc
@Import(MockBeansConfig.class)
@ActiveProfiles("test")
class ProjectControllerTest {

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
        String userId = "123";
        when(projectService.register(projectFormRequest,userId)).thenReturn(response);

        //when && then
        mockMvc.perform(post("/api/project/add")
                .header("X-User-ID",userId)
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
        mockMvc.perform(get("/api/project/all")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projects[0].title").value("테스트 프로젝트 A"));
    }

    @Test
    void 변경된_프로젝트를_정상적으로_갱신한다() throws Exception{
        //given
        ProjectFormRequest request = ProjectFormRequest.builder()
                .title("변경된_제목")
                .build();

        ProjectUpdateResponse response = ProjectUpdateResponse.from("변경된_제목", "변경이 완료되었습니다.");
        Long projectId = 1L;

        when(projectService.update(projectId, request)).thenReturn(response);

        //when && then
        mockMvc.perform(put("/api/project/"+projectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("변경된_제목"));
    }

    @Test
    void 특정_프로젝트를_정상적으로_반환한다() throws Exception {
        //given
        Long projectId = 1L;
        Project project = createProject("테스트_제목", 123L);
        ProjectSpecifyInfo response = ProjectSpecifyInfo.from(project);
        when(projectService.findOne(projectId)).thenReturn(response);

        //when && then
        mockMvc.perform(get("/api/project/"+projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("테스트_제목"));
    }

    @Test
    void 검색조건에_따라_프로젝트를_정상적으로_조회한다() throws Exception {
        // given
        String keyword = "테스트";
        String category = Category.REVERSING.name(); // enum name
        String status = "IN_PROGRESS"; // enum name
        String sort = "createdAt";
        String direction = "desc";
        int page = 1;
        int size = 10;

        ProjectPageResponse response = createProjectPageResponse();

        when(projectService.search(
                eq(keyword),
                eq(Category.valueOf(category)),
                eq(ProjectStatus.valueOf(status)),
                any(Pageable.class))
        ).thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/project/search")
                        .param("keyword", keyword)
                        .param("category", category)
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