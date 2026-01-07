package com.sejong.projectservice.domains.subgoal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sejong.projectservice.domains.collaborator.domain.CollaboratorEntity;
import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import com.sejong.projectservice.domains.project.repository.ProjectRepository;
import com.sejong.projectservice.domains.subgoal.domain.SubGoalEntity;
import com.sejong.projectservice.domains.subgoal.dto.SubGoalRequest;
import com.sejong.projectservice.domains.subgoal.repository.SubGoalRepository;
import com.sejong.projectservice.support.common.constants.ProjectStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("서브 목표 통합 테스트")
public class SubGoalIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private SubGoalRepository subGoalRepository;

    @BeforeEach
    void setUp() {
        projectRepository.deleteAll();
        subGoalRepository.deleteAll();
    }

    @Test
    @DisplayName("프로젝트 소유주가 서브 목표를 생설할 수 있다.")
    void 프로젝트_소우주가_서브_목표를_생성할_수_있다() throws Exception {
        //given
        ProjectEntity project = createProject("tbntb-1", "프로젝트 제목", "프로젝트 설명");
        ProjectEntity savedProject = projectRepository.save(project);
        Long projectId = savedProject.getId();

        SubGoalRequest request = SubGoalRequest.builder()
                .content("서브 목표 내용")
                .build();

        // when & then
        mockMvc.perform(post("/api/subgoal/{projectId}", projectId)
                        .header("X-User-Id", "tbntb-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("서브 목표 내용"))
                .andExpect(jsonPath("$.completed").value(false))
                .andExpect(jsonPath("$.message").value("하위 목표 추가 성공"));
    }

    @Test
    @DisplayName("프로젝트 협력자가 서브 목표를 생성할 수 있다.")
    void 프로젝트_협력자가_서브_목표를_생성할_수_있다() throws Exception {
        //given
        ProjectEntity project = createProject("tbntb-1", "프로젝트 제목", "프로젝트 설명");
        CollaboratorEntity collaborator = CollaboratorEntity.of("tbntb-2", project);
        ProjectEntity savedProject = projectRepository.save(project);
        Long projectId = savedProject.getId();

        SubGoalRequest request = SubGoalRequest.builder()
                .content("협력자가 추가한 서브 목표")
                .build();

        //when && then
        mockMvc.perform(post("/api/subgoal/{projectId}", projectId)
                        .header("X-User-Id", "tbntb-2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("협력자가 추가한 서브 목표"))
                .andExpect(jsonPath("$.completed").value(false))
                .andExpect(jsonPath("$.message").value("하위 목표 추가 성공"));
    }

    @Test
    @DisplayName("서브 목표 전체를 조회할 수 있다.")
    void 서브_목표_전체를_조회할_수_있다() throws Exception {
        //given
        ProjectEntity project = createProject("tbntb-1", "프로젝트 제목", "프로젝트 설명");
        ProjectEntity savedProject = projectRepository.save(project);
        Long projectId = savedProject.getId();

        SubGoalEntity subGoal1 = SubGoalEntity.of("서브 목표 1", false, LocalDateTime.now(), LocalDateTime.now(), savedProject);
        SubGoalEntity subGoal2 = SubGoalEntity.of("서브 목표 2", true, LocalDateTime.now(), LocalDateTime.now(), savedProject);
        subGoalRepository.save(subGoal1);
        subGoalRepository.save(subGoal2);

        //when && then
        mockMvc.perform(get("/api/subgoal/{projectId}", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].content").value(Matchers.containsInAnyOrder("서브 목표 1", "서브 목표 2")));
    }

    @Test
    @DisplayName("서브 목표를 체크(완료/미완료 토글)할 수 있다.")
    void 서브_목표를_체크할_수_있다() throws Exception {
        //given
        ProjectEntity project = createProject("tbntb-1", "프로젝트 제목", "프로젝트 설명");
        ProjectEntity savedProject = projectRepository.save(project);
        Long projectId = savedProject.getId();

        SubGoalEntity subGoal = SubGoalEntity.of("서브 목표", false, LocalDateTime.now(), LocalDateTime.now(), savedProject);
        SubGoalEntity savedSubGoal = subGoalRepository.save(subGoal);
        Long subGoalId = savedSubGoal.getId();

        //when && then - 미완료 -> 완료
        mockMvc.perform(put("/api/subgoal/check/{projectId}", projectId)
                        .header("X-User-Id", "tbntb-1")
                        .param("subGoalId", String.valueOf(subGoalId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isCheck").value(true))
                .andExpect(jsonPath("$.content").value("서브 목표"))
                .andExpect(jsonPath("$.message").value("체크 선택 or 미선택 완료"));

        // when & then - 완료 -> 미완료
        mockMvc.perform(put("/api/subgoal/check/{projectId}", projectId)
                        .header("X-User-Id", "tbntb-1")
                        .param("subGoalId", String.valueOf(subGoalId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isCheck").value(false))
                .andExpect(jsonPath("$.content").value("서브 목표"))
                .andExpect(jsonPath("$.message").value("체크 선택 or 미선택 완료"));
    }

    @Test
    @DisplayName("프로젝트 협력자가 서브 목표를 체크할 수 있다.")
    void 프로젝트_협력자가_서브_목표를_체크할_수_있다() throws Exception {
        //given
        ProjectEntity project = createProject("tbntb-1", "프로젝트 제목", "프로젝트 설명");
        CollaboratorEntity collaborator = CollaboratorEntity.of("tbntb-2", project);
        ProjectEntity savedProject = projectRepository.save(project);
        Long projectId = savedProject.getId();

        SubGoalEntity subGoal = SubGoalEntity.of("서브 목표", false, LocalDateTime.now(), LocalDateTime.now(), savedProject);
        SubGoalEntity savedSubGoal = subGoalRepository.save(subGoal);
        Long subGoalId = savedSubGoal.getId();

        //when && then
        mockMvc.perform(put("/api/subgoal/check/{projectId}",projectId)
                .header("X-User-Id","tbntb-2")
                .param("subGoalId",String.valueOf(subGoalId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isCheck").value(true))
                .andExpect(jsonPath("$.content").value("서브 목표"))
                .andExpect(jsonPath("$.message").value("체크 선택 or 미선택 완료"));
    }

    @Test
    @DisplayName("서브 목표를 삭제할 수 있다.")
    void 서브_목표를_삭제할_수_있다() throws Exception {
        //given
        ProjectEntity project = createProject("tbntb-1", "프로젝트 제목", "프로젝트 설명");
        ProjectEntity savedProject = projectRepository.save(project);
        Long projectId = savedProject.getId();

        SubGoalEntity subGoal = SubGoalEntity.of("서브 목표", false, LocalDateTime.now(), LocalDateTime.now(), savedProject);
        SubGoalEntity savedSubGoal = subGoalRepository.save(subGoal);
        Long subGoalId = savedSubGoal.getId();

        //when && then
        mockMvc.perform(delete("/api/subgoal/{projectId}/{subGoalId}", projectId, subGoalId)
                        .header("X-User-Id", "tbntb-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(subGoalId))
                .andExpect(jsonPath("$.message").value("삭제 되었습니다."));

        // 삭제된 서브 목표가 조회되지 않는지 확인
        mockMvc.perform(get("/api/subgoal/{projectId}", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("프로젝트 협력자가 서브 목표를 삭제할 수 있다.")
    void 프로젝트_협력자가_서브_목표를_삭제할_수_있다() throws Exception {
        //given
        ProjectEntity project = createProject("tbntb-1", "프로젝트 제목", "프로젝트 설명");
        CollaboratorEntity collaborator = CollaboratorEntity.of("tbntb-2", project);
        ProjectEntity savedProject = projectRepository.save(project);
        Long projectId = savedProject.getId();

        SubGoalEntity subGoal = SubGoalEntity.of("서브 목표", false, LocalDateTime.now(), LocalDateTime.now(), savedProject);
        SubGoalEntity savedSubGoal = subGoalRepository.save(subGoal);
        Long subGoalId = savedSubGoal.getId();

        //when && then
        mockMvc.perform(delete("/api/subgoal/{projectId}/{subGoalId}", projectId, subGoalId)
                        .header("X-User-Id", "tbntb-2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(subGoalId))
                .andExpect(jsonPath("$.message").value("삭제 되었습니다."));

        // 삭제된 서브 목표가 조회되지 않는지 확인
        mockMvc.perform(get("/api/subgoal/{projectId}", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("존재하지 않는 프로젝트에 대해 서브 목표를 생성하려고 하면 에러가 발생한다.")
    void 존재하지_않는_프로젝트에_대해_서브_목표를_생성하려고_하면_에러가_발생한다() throws Exception {
        //given
        Long nonExistentProjectId = 999L;
        SubGoalRequest request = SubGoalRequest.builder()
                .content("서브 목표 내용")
                .build();

        //when && then
        mockMvc.perform(post("/api/subgoal/{projectId}", nonExistentProjectId)
                        .header("X-User-Id", "tbntb-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("소유자가 아닌 사용자가 서브 목표를 생성하려고 하면 에러가 발생한다")
    void 소유자가_아닌_사용자가_서브_목표를_생성하려고_하면_에러가_발생한다() throws Exception {
        //given
        ProjectEntity project = createProject("tbntb-1", "프로젝트 제목", "프로젝트 설명");
        ProjectEntity savedProject = projectRepository.save(project);
        Long projectId = savedProject.getId();

        SubGoalRequest request = SubGoalRequest.builder()
                .content("서브 목표 내용")
                .build();

        //when && then
        mockMvc.perform(post("/api/subgoal/{projectId}", projectId)
                .header("X-User-Id", "unauthorized-user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("존재하지 않는 프로젝트에 서브 목표를 삭제하려고 하면 에러가 발생한다")
    void 존재하지_않는_프로젝트에_서브_목표를_삭제하려고_하면_에러가_발생한다() throws Exception {
        //given
        Long nonExistentProjectId = 999L;
        Long subGoalId = 1L;

        //when && then
        mockMvc.perform(delete("/api/subgoal/{projectId}/{subGoalId}", nonExistentProjectId, subGoalId)
                        .header("X-User-Id", "tbntb-1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("존재하지 않는 서브 목표를 삭제하려고 하면 에러가 발생한다.")
    void 존재하지_않는_서브_목표를_삭제하려고_하면_에러가_발생한다() throws Exception {
        //given
        ProjectEntity project = createProject("tbntb-1", "프로젝트 제목", "프로젝트 설명");
        ProjectEntity savedProject = projectRepository.save(project);
        Long projectId = savedProject.getId();

        Long nonExistentSubGoalId = 999L;

        //when && then
        mockMvc.perform(delete("/api/subgoal/{projectId}/{subGoalId}", projectId, nonExistentSubGoalId)
                        .header("X-User-Id", "tbntb-1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("소유자가 아닌 사용자가 서브 목표를 삭제하려고 하면 에러가 발생한다.")
    void 소유자가_아닌_사용자가_서브_목표를_삭제하려고_하면_에러가_발생한다() throws Exception {
        //given
        ProjectEntity project = createProject("tbntb-1", "프로젝트 제목", "프로젝트 설명");
        ProjectEntity savedProject = projectRepository.save(project);
        Long projectId = savedProject.getId();

        SubGoalEntity subGoal = SubGoalEntity.of("서브 목표", false, LocalDateTime.now(), LocalDateTime.now(), savedProject);
        SubGoalEntity savedSubGoal = subGoalRepository.save(subGoal);
        Long subGoalId = savedSubGoal.getId();

        //when && then
        mockMvc.perform(delete("/api/subgoal/{projectId}/{subGoalId}", projectId, subGoalId)
                        .header("X-User-Id", "unauthorized-user"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("서브 목표가 없는 프로젝트의 서브 목표를 조회하면 빈 배열이 반환된다.")
    void 서브_목표가_없는_프로젝트의_서브_목표를_조회하면_빈_배열이_반환된다() throws Exception {
        //given
        ProjectEntity project = createProject("tbntb-1", "프로젝트 제목", "프로젝트 설명");
        ProjectEntity savedProject = projectRepository.save(project);
        Long projectId = savedProject.getId();

        //when && then
        mockMvc.perform(get("/api/subgoal/{projectId}", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("존재하지 않는 프로젝트의 서브 목표를 조회하면 빈 배열이 반환된다.")
    void 존재하지_않는_프로젝트의_서브_목표를_조회하면_빈_배열이_반환된다() throws Exception {
        //given
        Long nonExistentProjectId = 999L;

        //when && then
        mockMvc.perform(get("/api/subgoal/{projectId}", nonExistentProjectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    private ProjectEntity createProject(String username, String title, String description) {
        return ProjectEntity.builder()
                .title(title)
                .description(description)
                .username(username)
                .nickname("nickname")
                .realname("realname")
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
