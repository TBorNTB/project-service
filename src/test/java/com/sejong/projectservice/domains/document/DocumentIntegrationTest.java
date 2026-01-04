package com.sejong.projectservice.domains.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sejong.projectservice.domains.collaborator.domain.CollaboratorEntity;
import com.sejong.projectservice.domains.document.domain.DocumentEntity;
import com.sejong.projectservice.domains.document.dto.DocumentCreateReq;
import com.sejong.projectservice.domains.document.dto.DocumentUpdateReq;
import com.sejong.projectservice.domains.document.repository.DocumentRepository;
import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import com.sejong.projectservice.domains.project.repository.ProjectRepository;
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
@DisplayName("문서 통합 테스트")
public class DocumentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @MockitoBean
    private UserExternalService userExternalService;

    @BeforeEach
    void setUp() {
        documentRepository.deleteAll();
        projectRepository.deleteAll();

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
    @DisplayName("프로젝트 소유자가 문서를 생성할 수 있다.")
    void 프로젝트_소유자가_문서를_생성할_수_있다() throws Exception {
        //given
        ProjectEntity project = createProject("tbntb-1", "프로젝트 제목", "프로젝트 설명");
        ProjectEntity savedProject = projectRepository.save(project);
        Long projectId = savedProject.getId();

        DocumentCreateReq request = DocumentCreateReq.builder()
                .title("문서 제목")
                .description("문서 설명")
                .content("문서 내용")
                .thumbnailUrl("thumbnail-url")
                .build();

        //when & then
        mockMvc.perform(post("/api/document/{projectId}", projectId)
                        .header("X-User-Id", "tbntb-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("문서 제목"))
                .andExpect(jsonPath("$.description").value("문서 설명"))
                .andExpect(jsonPath("$.content").value("문서 내용"))
                .andExpect(jsonPath("$.thumbnailUrl").value("thumbnail-url"))
                .andExpect(jsonPath("$.yorkieDocumentId").exists())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    @DisplayName("프로젝트 협력자가 문서를 생성할 수 있다.")
    void 프로젝트_협력자가_문서를_생성할_수_있다() throws Exception {
        //given
        ProjectEntity project = createProject("tbntb-1", "프로젝트 제목", "프로젝트 설명");
        CollaboratorEntity collaborator = CollaboratorEntity.of("tbntb-2", project);
        project.addCollaborator(collaborator);
        ProjectEntity savedProject = projectRepository.save(project);
        Long projectId = savedProject.getId();

        DocumentCreateReq request = DocumentCreateReq.builder()
                .title("문서 제목")
                .description("문서 설명")
                .content("문서 내용")
                .thumbnailUrl("thumbnail-url")
                .build();

        //when & then
        mockMvc.perform(post("/api/document/{projectId}", projectId)
                        .header("X-User-Id", "tbntb-2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("문서 제목"));
    }

    @Test
    @DisplayName("권한이 없는 사용자가 문서를 생성하려고 하면 에러가 발생한다.")
    void 권한이_없는_사용자가_문서를_생성하려고_하면_에러가_발생한다() throws Exception {
        //given
        ProjectEntity project = createProject("tbntb-1", "프로젝트 제목", "프로젝트 설명");
        ProjectEntity savedProject = projectRepository.save(project);
        Long projectId = savedProject.getId();

        DocumentCreateReq request = DocumentCreateReq.builder()
                .title("문서 제목")
                .description("문서 설명")
                .content("문서 내용")
                .thumbnailUrl("thumbnail-url")
                .build();

        //when & then
        mockMvc.perform(post("/api/document/{projectId}", projectId)
                        .header("X-User-Id", "unauthorized-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("존재하지 않는 프로젝트에 문서를 생성하려고 하면 에러가 발생한다.")
    void 존재하지_않는_프로젝트에_문서를_생성하려고_하면_에러가_발생한다() throws Exception {
        //given
        Long nonExistentProjectId = 999L;

        DocumentCreateReq request = DocumentCreateReq.builder()
                .title("문서 제목")
                .description("문서 설명")
                .content("문서 내용")
                .thumbnailUrl("thumbnail-url")
                .build();

        //when & then
        mockMvc.perform(post("/api/document/{projectId}", nonExistentProjectId)
                        .header("X-User-Id", "tbntb-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("문서를 조회할 수 있다.")
    void 문서를_조회할_수_있다() throws Exception {
        //given
        ProjectEntity project = createProject("tbntb-1", "프로젝트 제목", "프로젝트 설명");
        ProjectEntity savedProject = projectRepository.save(project);

        DocumentEntity document = createDocument(savedProject, "문서 제목", "문서 설명", "문서 내용", "thumbnail-url");
        DocumentEntity savedDocument = documentRepository.save(document);
        Long documentId = savedDocument.getId();

        //when & then
        mockMvc.perform(get("/api/document/{documentId}", documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(documentId))
                .andExpect(jsonPath("$.title").value("문서 제목"))
                .andExpect(jsonPath("$.description").value("문서 설명"))
                .andExpect(jsonPath("$.content").value("문서 내용"))
                .andExpect(jsonPath("$.thumbnailUrl").value("thumbnail-url"))
                .andExpect(jsonPath("$.yorkieDocumentId").exists())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    @DisplayName("존재하지 않는 문서를 조회하려고 하면 에러가 발생한다.")
    void 존재하지_않는_문서를_조회하려고_하면_에러가_발생한다() throws Exception {
        //given
        Long nonExistentDocumentId = 999L;

        //when & then
        mockMvc.perform(get("/api/document/{documentId}", nonExistentDocumentId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("프로젝트 소유자가 문서를 수정할 수 있다.")
    void 프로젝트_소유자가_문서를_수정할_수_있다() throws Exception {
        //given
        ProjectEntity project = createProject("tbntb-1", "프로젝트 제목", "프로젝트 설명");
        ProjectEntity savedProject = projectRepository.save(project);

        DocumentEntity document = createDocument(savedProject, "문서 제목", "문서 설명", "문서 내용", "thumbnail-url");
        DocumentEntity savedDocument = documentRepository.save(document);
        Long documentId = savedDocument.getId();

        DocumentUpdateReq updateRequest = DocumentUpdateReq.builder()
                .title("수정된 제목")
                .description("수정된 설명")
                .content("수정된 내용")
                .thumbnailUrl("수정된-thumbnail-url")
                .build();

        //when & then
        mockMvc.perform(put("/api/document/{documentId}", documentId)
                        .header("X-User-Id", "tbntb-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(documentId))
                .andExpect(jsonPath("$.title").value("수정된 제목"))
                .andExpect(jsonPath("$.description").value("수정된 설명"))
                .andExpect(jsonPath("$.content").value("수정된 내용"))
                .andExpect(jsonPath("$.thumbnailUrl").value("수정된-thumbnail-url"));

        // 수정된 내용이 반영되었는지 확인
        mockMvc.perform(get("/api/document/{documentId}", documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("수정된 제목"))
                .andExpect(jsonPath("$.description").value("수정된 설명"))
                .andExpect(jsonPath("$.content").value("수정된 내용"));
    }

    @Test
    @DisplayName("프로젝트 협력자가 문서를 수정할 수 있다.")
    void 프로젝트_협력자가_문서를_수정할_수_있다() throws Exception {
        //given
        ProjectEntity project = createProject("tbntb-1", "프로젝트 제목", "프로젝트 설명");
        CollaboratorEntity collaborator = CollaboratorEntity.of("tbntb-2", project);
        project.addCollaborator(collaborator);
        ProjectEntity savedProject = projectRepository.save(project);

        DocumentEntity document = createDocument(savedProject, "문서 제목", "문서 설명", "문서 내용", "thumbnail-url");
        DocumentEntity savedDocument = documentRepository.save(document);
        Long documentId = savedDocument.getId();

        DocumentUpdateReq updateRequest = DocumentUpdateReq.builder()
                .title("수정된 제목")
                .description("수정된 설명")
                .content("수정된 내용")
                .thumbnailUrl("수정된-thumbnail-url")
                .build();

        //when & then
        mockMvc.perform(put("/api/document/{documentId}", documentId)
                        .header("X-User-Id", "tbntb-2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("수정된 제목"));
    }

    @Test
    @DisplayName("권한이 없는 사용자가 문서를 수정하려고 하면 에러가 발생한다.")
    void 권한이_없는_사용자가_문서를_수정하려고_하면_에러가_발생한다() throws Exception {
        //given
        ProjectEntity project = createProject("tbntb-1", "프로젝트 제목", "프로젝트 설명");
        ProjectEntity savedProject = projectRepository.save(project);

        DocumentEntity document = createDocument(savedProject, "문서 제목", "문서 설명", "문서 내용", "thumbnail-url");
        DocumentEntity savedDocument = documentRepository.save(document);
        Long documentId = savedDocument.getId();

        DocumentUpdateReq updateRequest = DocumentUpdateReq.builder()
                .title("수정된 제목")
                .description("수정된 설명")
                .content("수정된 내용")
                .thumbnailUrl("수정된-thumbnail-url")
                .build();

        //when & then
        mockMvc.perform(put("/api/document/{documentId}", documentId)
                        .header("X-User-Id", "unauthorized-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("존재하지 않는 문서를 수정하려고 하면 에러가 발생한다.")
    void 존재하지_않는_문서를_수정하려고_하면_에러가_발생한다() throws Exception {
        //given
        Long nonExistentDocumentId = 999L;

        DocumentUpdateReq updateRequest = DocumentUpdateReq.builder()
                .title("수정된 제목")
                .description("수정된 설명")
                .content("수정된 내용")
                .thumbnailUrl("수정된-thumbnail-url")
                .build();

        //when & then
        mockMvc.perform(put("/api/document/{documentId}", nonExistentDocumentId)
                        .header("X-User-Id", "tbntb-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("프로젝트 소유자가 문서를 삭제할 수 있다.")
    void 프로젝트_소유자가_문서를_삭제할_수_있다() throws Exception {
        //given
        ProjectEntity project = createProject("tbntb-1", "프로젝트 제목", "프로젝트 설명");
        ProjectEntity savedProject = projectRepository.save(project);

        DocumentEntity document = createDocument(savedProject, "문서 제목", "문서 설명", "문서 내용", "thumbnail-url");
        DocumentEntity savedDocument = documentRepository.save(document);
        Long documentId = savedDocument.getId();

        //when & then
        mockMvc.perform(delete("/api/document/{documentId}", documentId)
                        .header("X-User-Id", "tbntb-1"))
                .andExpect(status().isOk());

        // 삭제된 문서가 조회되지 않는지 확인
        mockMvc.perform(get("/api/document/{documentId}", documentId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("프로젝트 협력자가 문서를 삭제할 수 있다.")
    void 프로젝트_협력자가_문서를_삭제할_수_있다() throws Exception {
        //given
        ProjectEntity project = createProject("tbntb-1", "프로젝트 제목", "프로젝트 설명");
        CollaboratorEntity collaborator = CollaboratorEntity.of("tbntb-2", project);
        project.addCollaborator(collaborator);
        ProjectEntity savedProject = projectRepository.save(project);

        DocumentEntity document = createDocument(savedProject, "문서 제목", "문서 설명", "문서 내용", "thumbnail-url");
        DocumentEntity savedDocument = documentRepository.save(document);
        Long documentId = savedDocument.getId();

        //when & then
        mockMvc.perform(delete("/api/document/{documentId}", documentId)
                        .header("X-User-Id", "tbntb-2"))
                .andExpect(status().isOk());

        // 삭제된 문서가 조회되지 않는지 확인
        mockMvc.perform(get("/api/document/{documentId}", documentId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("권한이 없는 사용자가 문서를 삭제하려고 하면 에러가 발생한다.")
    void 권한이_없는_사용자가_문서를_삭제하려고_하면_에러가_발생한다() throws Exception {
        //given
        ProjectEntity project = createProject("tbntb-1", "프로젝트 제목", "프로젝트 설명");
        ProjectEntity savedProject = projectRepository.save(project);

        DocumentEntity document = createDocument(savedProject, "문서 제목", "문서 설명", "문서 내용", "thumbnail-url");
        DocumentEntity savedDocument = documentRepository.save(document);
        Long documentId = savedDocument.getId();

        //when & then
        mockMvc.perform(delete("/api/document/{documentId}", documentId)
                        .header("X-User-Id", "unauthorized-user"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("존재하지 않는 문서를 삭제하려고 하면 에러가 발생한다.")
    void 존재하지_않는_문서를_삭제하려고_하면_에러가_발생한다() throws Exception {
        //given
        Long nonExistentDocumentId = 999L;

        //when & then
        mockMvc.perform(delete("/api/document/{documentId}", nonExistentDocumentId)
                        .header("X-User-Id", "tbntb-1"))
                .andExpect(status().isNotFound());
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

    private DocumentEntity createDocument(ProjectEntity project, String title, String description, String content, String thumbnailUrl) {
        DocumentEntity document = DocumentEntity.builder()
                .yorkieDocumentId("yorkie-" + System.currentTimeMillis())
                .title(title)
                .description(description)
                .content(content)
                .thumbnailUrl(thumbnailUrl)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .projectEntity(project)
                .build();
        project.addDocument(document);
        return document;
    }
}

