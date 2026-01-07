package com.sejong.projectservice.domains.project;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sejong.projectservice.domains.category.domain.CategoryEntity;
import com.sejong.projectservice.domains.category.repository.CategoryRepository;
import com.sejong.projectservice.domains.collaborator.domain.CollaboratorEntity;
import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import com.sejong.projectservice.domains.project.dto.request.ProjectFormRequest;
import com.sejong.projectservice.domains.project.dto.request.ProjectUpdateRequest;
import com.sejong.projectservice.domains.project.repository.ProjectRepository;
import com.sejong.projectservice.domains.techstack.domain.TechStackEntity;
import com.sejong.projectservice.domains.techstack.repository.TechStackRepository;
import com.sejong.projectservice.support.common.constants.ProjectStatus;
import com.sejong.projectservice.support.common.internal.UserExternalService;
import com.sejong.projectservice.support.common.internal.response.UserNameInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("프로젝트 통합 테스트")
public class ProjectIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TechStackRepository techStackRepository;

    @MockitoBean
    private UserExternalService userExternalService;

    @BeforeEach
    void setUp() {
        projectRepository.deleteAll();
        categoryRepository.deleteAll();
        techStackRepository.deleteAll();

        // UserExternalService 모킹 설정
        doNothing().when(userExternalService).validateExistence(any(String.class), anyList());
        when(userExternalService.getUserNameInfos(anyList())).thenAnswer(invocation -> {
            List<String> usernames = invocation.getArgument(0);
            Map<String, UserNameInfo> result = new HashMap<>();
            for (String username : usernames) {
                result.put(username, new UserNameInfo("nickname-" + username, "realname-" + username));
            }
            return result;
        });
    }

    @Test
    @DisplayName("프로젝트를 생성할 수 있다.")
    void 프로젝트를_생성할_수_있다() throws Exception {
        //given
        ProjectFormRequest request = ProjectFormRequest.builder()
                .title("프로젝트 제목")
                .description("프로젝트 설명")
                .thumbnail("thumbnail-url")
                .projectStatus(ProjectStatus.IN_PROGRESS)
                .categories(List.of("WEB-HACKING", "DEVICE-HACKING"))
                .techStacks(List.of("Java", "Spring"))
                .collaborators(List.of("tbntb-1", "tbntb-2"))
                .subGoals(List.of("서브 목표 1", "서브 목표 2"))
                .build();
        //when && then
        mockMvc.perform(post("/api/project")
                        .header("X-User-Id", "tbntb-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("프로젝트 제목"))
                .andExpect(jsonPath("$.message").value("저장 완료"));
    }

    @Test
    @DisplayName("프로젝트를 조회할 수 있다.")
    void 프로젝트를_조회할_수_있다() throws Exception{
        //given
        ProjectEntity project = createProject("tbntb-1", "프로젝트 제목", "프로젝트 설명");
        ProjectEntity savedProject = projectRepository.save(project);
        Long projectId = savedProject.getId();

        //when && then
        mockMvc.perform(get("/api/project/{projectId}", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(projectId))
                .andExpect(jsonPath("$.title").value("프로젝트 제목"))
                .andExpect(jsonPath("$.description").value("프로젝트 설명"))
                .andExpect(jsonPath("$.username").value("tbntb-1"));
    }

    @Test
    @DisplayName("존재하지 않는 프로젝트를 조회하려고 하면 에러가 발생한다.")
    void 존재하지_않는_프로젝트를_조회하려고_하면_에러가_발생한다() throws Exception{
        //given
        Long nonExistentProjectId = 999L;

        //when & then
        mockMvc.perform(get("/api/project/{projectId}", nonExistentProjectId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("프로젝트 목록을 페이지네이션으로 조회할 수 있다.")
    void 프로젝트_목록을_페이지네이션으로_조회할_수_있다() throws Exception {
        //given
        for (int i = 1; i <= 10; i++) {
            ProjectEntity project = createProject("tbntb-" + i, "프로젝트 제목 " + i, "프로젝트 설명 " + i);
            projectRepository.save(project);
        }

        //when & then
        mockMvc.perform(get("/api/project")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projects").isArray())
                .andExpect(jsonPath("$.projects.length()").value(5))
                .andExpect(jsonPath("$.totalElements").value(10));
    }

    @Test
    @DisplayName("프로젝트 소유자가 프로젝트를 수정할 수 있다.")
    void 프로젝트_소유자가_프로젝트를_수정할_수_있다() throws Exception {
        //given
        ProjectEntity project = createProject("tbntb-1", "프로젝트 제목", "프로젝트 설명");
        ProjectEntity savedProject = projectRepository.save(project);
        Long projectId = savedProject.getId();

        ProjectUpdateRequest updateRequest = ProjectUpdateRequest.builder()
                .title("수정된 제목")
                .description("수정된 설명")
                .projectStatus(ProjectStatus.COMPLETED)
                .thumbnailUrl("updated-thumbnail-url")
                .build();

        //when & then
        mockMvc.perform(put("/api/project/{projectId}", projectId)
                        .header("X-User-Id", "tbntb-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("수정된 제목"))
                .andExpect(jsonPath("$.message").value("수정 완료"));

        // 수정된 내용이 반영되었는지 확인
        mockMvc.perform(get("/api/project/{projectId}", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("수정된 제목"))
                .andExpect(jsonPath("$.description").value("수정된 설명"));
    }

    @Test
    @DisplayName("프로젝트 협력자가 프로젝트를 수정할 수 있다.")
    void 프로젝트_협력자가_프로젝트를_수정할_수_있다() throws Exception {
        //given
        ProjectEntity project = createProject("tbntb-1", "프로젝트 제목", "프로젝트 설명");
        CollaboratorEntity.of("tbntb-2", project); // of 메서드가 이미 addCollaborator를 호출함
        ProjectEntity savedProject = projectRepository.save(project);
        Long projectId = savedProject.getId();

        ProjectUpdateRequest updateRequest = ProjectUpdateRequest.builder()
                .title("협력자가 수정한 제목")
                .description("협력자가 수정한 설명")
                .projectStatus(ProjectStatus.IN_PROGRESS)
                .thumbnailUrl("collaborator-thumbnail-url")
                .build();

        //when & then
        mockMvc.perform(put("/api/project/{projectId}", projectId)
                        .header("X-User-Id", "tbntb-2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("협력자가 수정한 제목"))
                .andExpect(jsonPath("$.message").value("수정 완료"));

        // 수정된 내용이 반영되었는지 확인
        mockMvc.perform(get("/api/project/{projectId}", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("협력자가 수정한 제목"))
                .andExpect(jsonPath("$.description").value("협력자가 수정한 설명"));
    }

    @Test
    @DisplayName("소유자가 아닌 사용자가 프로젝트를 수정하려고 하면 에러가 발생한다.")
    void 소유자가_아닌_사용자가_프로젝트를_수정하려고_하면_에러가_발생한다() throws Exception {
        //given
        ProjectEntity project = createProject("tbntb-1", "프로젝트 제목", "프로젝트 설명");
        ProjectEntity savedProject = projectRepository.save(project);
        Long projectId = savedProject.getId();

        ProjectUpdateRequest updateRequest = ProjectUpdateRequest.builder()
                .title("수정된 제목")
                .description("수정된 설명")
                .projectStatus(ProjectStatus.COMPLETED)
                .thumbnailUrl("updated-thumbnail-url")
                .build();

        //when & then
        mockMvc.perform(put("/api/project/{projectId}", projectId)
                        .header("X-User-Id", "unauthorized-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("존재하지 않는 프로젝트를 수정하려고 하면 에러가 발생한다.")
    void 존재하지_않는_프로젝트를_수정하려고_하면_에러가_발생한다() throws Exception {
        //given
        Long nonExistentProjectId = 999L;

        ProjectUpdateRequest updateRequest = ProjectUpdateRequest.builder()
                .title("수정된 제목")
                .description("수정된 설명")
                .projectStatus(ProjectStatus.COMPLETED)
                .thumbnailUrl("updated-thumbnail-url")
                .build();

        //when & then
        mockMvc.perform(put("/api/project/{projectId}", nonExistentProjectId)
                        .header("X-User-Id", "tbntb-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("프로젝트 소유자가 프로젝트를 삭제할 수 있다.")
    void 프로젝트_소유자가_프로젝트를_삭제할_수_있다() throws Exception {
        //given
        ProjectEntity project = createProject("tbntb-1", "프로젝트 제목", "프로젝트 설명");
        ProjectEntity savedProject = projectRepository.save(project);
        Long projectId = savedProject.getId();

        //when & then
        mockMvc.perform(delete("/api/project/{projectId}", projectId)
                        .header("X-User-Id", "tbntb-1")
                        .header("X-User-Role", "GUEST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("프로젝트 제목"))
                .andExpect(jsonPath("$.message").value("삭제 완료"));

        // 삭제된 프로젝트가 조회되지 않는지 확인
        mockMvc.perform(get("/api/project/{projectId}", projectId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("관리자가 프로젝트를 삭제할 수 있다.")
    void 관리자가_프로젝트를_삭제할_수_있다() throws Exception {
        //given
        ProjectEntity project = createProject("tbntb-1", "프로젝트 제목", "프로젝트 설명");
        ProjectEntity savedProject = projectRepository.save(project);
        Long projectId = savedProject.getId();

        //when & then
        mockMvc.perform(delete("/api/project/{projectId}", projectId)
                        .header("X-User-Id", "tbntb-999")
                        .header("X-User-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("프로젝트 제목"))
                .andExpect(jsonPath("$.message").value("삭제 완료"));

        // 삭제된 프로젝트가 조회되지 않는지 확인
        mockMvc.perform(get("/api/project/{projectId}", projectId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("소유자가 아닌 사용자가 프로젝트를 삭제하려고 하면 에러가 발생한다.")
    void 소유자가_아닌_사용자가_프로젝트를_삭제하려고_하면_에러가_발생한다() throws Exception {
        //given
        ProjectEntity project = createProject("tbntb-1", "프로젝트 제목", "프로젝트 설명");
        ProjectEntity savedProject = projectRepository.save(project);
        Long projectId = savedProject.getId();

        //when & then
        mockMvc.perform(delete("/api/project/{projectId}", projectId)
                        .header("X-User-Id", "tbntb-3")
                        .header("X-User-Role", "GUEST"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("존재하지 않는 프로젝트를 삭제하려고 하면 에러가 발생한다.")
    void 존재하지_않는_프로젝트를_삭제하려고_하면_에러가_발생한다() throws Exception {
        //given
        Long nonExistentProjectId = 999L;

        //when & then
        mockMvc.perform(delete("/api/project/{projectId}", nonExistentProjectId)
                        .header("X-User-Id", "tbntb-1")
                        .header("X-User-Role", "GUEST"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("키워드로 프로젝트를 검색할 수 있다.")
    void 키워드로_프로젝트를_검색할_수_있다() throws Exception {
        //given
        ProjectEntity project1 = createProject("tbntb-1", "백엔드 프로젝트", "Spring Boot 프로젝트");
        ProjectEntity project2 = createProject("tbntb-2", "프론트엔드 프로젝트", "React 프로젝트");
        ProjectEntity project3 = createProject("tbntb-3", "풀스택 프로젝트", "Spring + React");
        projectRepository.save(project1);
        projectRepository.save(project2);
        projectRepository.save(project3);

        //when & then
        mockMvc.perform(get("/api/project/search-page")
                        .param("keyword", "백엔드")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projects").isArray())
                .andExpect(jsonPath("$.projects.length()").value(1))
                .andExpect(jsonPath("$.projects[0].title").value("백엔드 프로젝트"));
    }

    @Test
    @DisplayName("상태로 프로젝트를 필터링할 수 있다.")
    void 상태로_프로젝트를_필터링할_수_있다() throws Exception {
        //given
        ProjectEntity project1 = createProjectWithStatus("tbntb-1", "진행중 프로젝트", ProjectStatus.IN_PROGRESS);
        ProjectEntity project2 = createProjectWithStatus("tbntb-2", "완료된 프로젝트", ProjectStatus.COMPLETED);
        ProjectEntity project3 = createProjectWithStatus("tbntb-3", "계획중 프로젝트", ProjectStatus.COMPLETED);
        projectRepository.save(project1);
        projectRepository.save(project2);
        projectRepository.save(project3);

        //when & then
        mockMvc.perform(get("/api/project/search-page")
                        .param("status", "IN_PROGRESS")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projects").isArray())
                .andExpect(jsonPath("$.projects.length()").value(1))
                .andExpect(jsonPath("$.projects[0].title").value("진행중 프로젝트"))
                .andExpect(jsonPath("$.projects[0].projectStatus").value("IN_PROGRESS"));
    }

    @Test
    @DisplayName("키워드와 상태로 프로젝트를 검색할 수 있다.")
    void 키워드와_상태로_프로젝트를_검색할_수_있다() throws Exception {
        //given
        ProjectEntity project1 = createProjectWithStatus("tbntb-1", "백엔드 프로젝트", ProjectStatus.IN_PROGRESS);
        ProjectEntity project2 = createProjectWithStatus("tbntb-2", "백엔드 프로젝트", ProjectStatus.COMPLETED);
        ProjectEntity project3 = createProjectWithStatus("tbntb-3", "프론트엔드 프로젝트", ProjectStatus.IN_PROGRESS);
        projectRepository.save(project1);
        projectRepository.save(project2);
        projectRepository.save(project3);

        //when & then
        mockMvc.perform(get("/api/project/search-page")
                        .param("keyword", "백엔드")
                        .param("status", "IN_PROGRESS")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projects").isArray())
                .andExpect(jsonPath("$.projects.length()").value(1))
                .andExpect(jsonPath("$.projects[0].title").value("백엔드 프로젝트"))
                .andExpect(jsonPath("$.projects[0].projectStatus").value("IN_PROGRESS"));
    }

    @Test
    @DisplayName("카테고리와 기술 스택이 포함된 프로젝트를 생성할 수 있다.")
    void 카테고리와_기술_스택이_포함된_프로젝트를_생성할_수_있다() throws Exception {
        //given
        CategoryEntity category1 = CategoryEntity.of("WEB-HACKING");
        CategoryEntity category2 = CategoryEntity.of("DEVICE-HACKING");
        categoryRepository.save(category1);
        categoryRepository.save(category2);

        TechStackEntity techStack1 = TechStackEntity.of("Java");
        TechStackEntity techStack2 = TechStackEntity.of("Spring");
        techStackRepository.save(techStack1);
        techStackRepository.save(techStack2);

        ProjectFormRequest request = ProjectFormRequest.builder()
                .title("풀스택 프로젝트")
                .description("프로젝트 설명")
                .thumbnail("thumbnail-url")
                .projectStatus(ProjectStatus.IN_PROGRESS)
                .categories(List.of("WEB-HACKING", "DEVICE-HACKING"))
                .techStacks(List.of("Java", "Spring"))
                .collaborators(new ArrayList<>())
                .subGoals(new ArrayList<>())
                .build();

        //when & then
        mockMvc.perform(post("/api/project")
                        .header("X-User-Id", "tbntb-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("풀스택 프로젝트"))
                .andExpect(jsonPath("$.message").value("저장 완료"));

        // 생성된 프로젝트 확인
        List<ProjectEntity> projects = projectRepository.findAll();
        assertEquals(1, projects.size());
        ProjectEntity savedProject = projects.get(0);
        assertEquals(2, savedProject.getProjectCategories().size());
        assertEquals(2, savedProject.getProjectTechStacks().size());
    }

    @Test
    @DisplayName("프로젝트 검색 시 정렬이 제대로 작동한다.")
    void 프로젝트_검색_시_정렬이_제대로_작동한다() throws Exception {
        //given
        for (int i = 1; i <= 5; i++) {
            ProjectEntity project = createProject("tbntb-" + i, "프로젝트 " + i, "설명 " + i);
            projectRepository.save(project);
        }

        //when & then - 내림차순 정렬 (기본값)
        String descResponse = mockMvc.perform(get("/api/project/search-page")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "id")
                        .param("direction", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projects").isArray())
                .andExpect(jsonPath("$.projects.length()").value(5))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode descNode = objectMapper.readTree(descResponse);
        JsonNode descContent = descNode.get("projects");
        Long descFirstId = descContent.get(0).get("id").asLong();
        Long descLastId = descContent.get(4).get("id").asLong();
        assertTrue(descFirstId > descLastId,
                "프로젝트 검색 내림차순 정렬 실패. 첫 번째 ID: " + descFirstId + ", 마지막 ID: " + descLastId);

        //when & then - 오름차순 정렬
        String ascResponse = mockMvc.perform(get("/api/project/search-page")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "id")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projects").isArray())
                .andExpect(jsonPath("$.projects.length()").value(5))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode ascNode = objectMapper.readTree(ascResponse);
        JsonNode ascContent = ascNode.get("projects");
        Long ascFirstId = ascContent.get(0).get("id").asLong();
        Long ascLastId = ascContent.get(4).get("id").asLong();
        assertTrue(ascFirstId < ascLastId,
                "프로젝트 검색 오름차순 정렬 실패. 첫 번째 ID: " + ascFirstId + ", 마지막 ID: " + ascLastId);
    }

    private ProjectEntity createProject(String username, String title, String description) {
        return ProjectEntity.builder()
                .title(title)
                .description(description)
                .username(username)
                .nickname("nickname-" + username)
                .realname("realname-" + username)
                .projectStatus(ProjectStatus.IN_PROGRESS)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .projectCategories(new ArrayList<>())
                .projectTechStacks(new ArrayList<>())
                .collaboratorEntities(new ArrayList<>())
                .subGoals(new ArrayList<>())
                .documentEntities(new ArrayList<>())
                .build();
    }

    private ProjectEntity createProjectWithStatus(String username, String title, ProjectStatus status) {
        return ProjectEntity.builder()
                .title(title)
                .description("프로젝트 설명")
                .username(username)
                .nickname("nickname-" + username)
                .realname("realname-" + username)
                .projectStatus(status)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .projectCategories(new ArrayList<>())
                .projectTechStacks(new ArrayList<>())
                .collaboratorEntities(new ArrayList<>())
                .subGoals(new ArrayList<>())
                .documentEntities(new ArrayList<>())
                .build();
    }
}
