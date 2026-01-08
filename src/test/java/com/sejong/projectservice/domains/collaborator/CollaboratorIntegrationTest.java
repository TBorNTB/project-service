package com.sejong.projectservice.domains.collaborator;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sejong.projectservice.domains.collaborator.domain.CollaboratorEntity;
import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import com.sejong.projectservice.domains.project.repository.ProjectRepository;
import com.sejong.projectservice.support.common.constants.ProjectStatus;
import com.sejong.projectservice.support.common.internal.UserExternalService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.Matchers;
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("협력자 통합 테스트")
public class CollaboratorIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProjectRepository projectRepository;

    @MockitoBean
    private UserExternalService userExternalService;

    @BeforeEach
    void setUp() {
        projectRepository.deleteAll();
        // UserExternalService의 validateExistence 메서드를 모킹하여 항상 성공하도록 설정
        doNothing().when(userExternalService).validateExistence(any(String.class), anyList());
    }

    @Test
    @DisplayName("프로젝트 소유주가 협력자를 추가할 수 있다.")
    void 프로젝트_소유주가_협력자를_추가할_수_있다() throws Exception {
        //given
        ProjectEntity project = createProject("tbntb-1", "프로젝트 제목", "프로젝트 설명");
        ProjectEntity savedProject = projectRepository.save(project);
        Long projectId = savedProject.getId();

        List<String> collaboratorNames = List.of("tbntb-2", "tbntb-3");

        //when && then
        mockMvc.perform(put("/api/collaborator/{projectId}", projectId)
                        .header("X-User-Id", "tbntb-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(collaboratorNames)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].collaboratorName").value(Matchers.containsInAnyOrder("tbntb-2", "tbntb-3")));
    }

    @Test
    @DisplayName("프로젝트 소유주가 협력자를 수정할 수 있다.")
    void 프로젝트_소유자가_협력자를_수정할_수_있다() throws Exception {
        //given
        ProjectEntity project = createProject("tbntb-1", "프로젝트 제목", "프로젝트 설명");
        CollaboratorEntity collaborator1 = CollaboratorEntity.of("tbntb-2", project);
        CollaboratorEntity collaborator2 = CollaboratorEntity.of("tbntb-3", project);
        ProjectEntity savedProject = projectRepository.save(project);
        Long projectId = savedProject.getId();

        // 기존 협력자들을 새로운 협력자로 교체
        List<String> newCollaboratorNames = List.of("tbntb-4", "tbntb-5");

        //when && then
        mockMvc.perform(put("/api/collaborator/{projectId}", projectId)
                        .header("X-User-Id", "tbntb-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCollaboratorNames)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].collaboratorName").value(Matchers.containsInAnyOrder("tbntb-4", "tbntb-5")));
    }

    @Test
    @DisplayName("프로젝트 협력자가 협력자를 수정할 수 있다.")
    void 프로젝트_협력자가_협력자를_수정할_수_있다() throws Exception {
        //given
        ProjectEntity project = createProject("tbntb-1", "프로젝트 제목", "프로젝트 설명");
        CollaboratorEntity collaborator = CollaboratorEntity.of("tbntb-2", project);
        ProjectEntity savedProject = projectRepository.save(project);
        Long projectId = savedProject.getId();

        List<String> newCollaboratorNames = List.of("tbntb-2", "tbntb-3");

        //when && then
        mockMvc.perform(put("/api/collaborator/{projectId}", projectId)
                        .header("X-User-Id", "tbntb-2") //협력자
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCollaboratorNames)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].collaboratorName").value(Matchers.containsInAnyOrder("tbntb-2", "tbntb-3")));
    }

    @Test
    @DisplayName("존재하지 않는 프로젝트에 대해 협력자를 수정하려고 하면 에러가 발생한다.")
    void 존재하지_않는_프로젝트에_대해_협력자를_수정하려고_하면_에러가_발생한다() throws Exception {
        //given
        Long nonExistentProjectId = 999L;
        List<String> collaboratorNames = List.of("tbntb-2", "tbntb-3");

        //when && then
        mockMvc.perform(put("/api/collaborator/{projectId}", nonExistentProjectId)
                        .header("X-User-Id", "tbntb-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(collaboratorNames)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("빈 리스트로 협력자를 업데이트할 수 있다 (모두 제거).")
    void 빈_리스트로_협력자를_업데이트할_수_있다() throws Exception {
        // given
        ProjectEntity project = createProject("tbntb-1", "프로젝트 제목", "프로젝트 설명");
        CollaboratorEntity collaborator1 = CollaboratorEntity.of("tbntb-2", project);
        CollaboratorEntity collaborator2 = CollaboratorEntity.of("tbntb-3", project);
        ProjectEntity savedProject = projectRepository.save(project);
        Long projectId = savedProject.getId();

        List<String> emptyList = new ArrayList<>();

        // when & then
        mockMvc.perform(put("/api/collaborator/{projectId}", projectId)
                        .header("X-User-Id", "tbntb-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyList)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("중복된 협력자 이름은 자동으로 제거된다.")
    void 중복된_협력자_이름은_자동으로_제거된다() throws Exception {
        //given
        ProjectEntity project = createProject("tbntb-1", "프로젝트 제목", "프로젝트 설명");
        ProjectEntity savedProject = projectRepository.save(project);
        Long projectId = savedProject.getId();

        List<String> collaboratorNamesWithDuplicates = List.of("tbntb-2", "tbntb-3", "tbntb-2", "tbntb-3");
        //when && then
        mockMvc.perform(put("/api/collaborator/{projectId}", projectId)
                        .header("X-User-Id", "tbntb-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(collaboratorNamesWithDuplicates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].collaboratorName").value(Matchers.containsInAnyOrder("tbntb-2", "tbntb-3")));
    }

    @Test
    @DisplayName("공백과 null이 포함된 협력자 이름은 필터링된다.")
    void 공백과_null이_포함된_협력자_이름은_필터링된다() throws Exception {
        //given
        ProjectEntity project = createProject("tbntb-1", "프로젝트 제목", "프로젝트 설명");
        ProjectEntity savedProject = projectRepository.save(project);
        Long projectId = savedProject.getId();

        List<String> collaboratorNamesWithBlanks = new ArrayList<>();
        collaboratorNamesWithBlanks.add("tbntb-2");
        collaboratorNamesWithBlanks.add("");
        collaboratorNamesWithBlanks.add("  ");
        collaboratorNamesWithBlanks.add("tbntb-3");
        collaboratorNamesWithBlanks.add(null);

        //when && then
        mockMvc.perform(put("/api/collaborator/{projectId}", projectId)
                        .header("X-User-Id", "tbntb-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(collaboratorNamesWithBlanks)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].collaboratorName").value(Matchers.containsInAnyOrder("tbntb-2", "tbntb-3")));
    }


    private ProjectEntity createProject(String username, String title, String description) {
        return ProjectEntity.builder()
                .title(title)
                .description(description)
                .username(username)
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
}
