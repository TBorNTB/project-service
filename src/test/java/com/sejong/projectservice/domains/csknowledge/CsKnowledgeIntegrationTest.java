package com.sejong.projectservice.domains.csknowledge;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sejong.projectservice.domains.category.domain.CategoryEntity;
import com.sejong.projectservice.domains.category.repository.CategoryRepository;
import com.sejong.projectservice.domains.csknowledge.domain.CsKnowledgeEntity;
import com.sejong.projectservice.domains.csknowledge.dto.CsKnowledgeReqDto;
import com.sejong.projectservice.domains.csknowledge.repository.CsKnowledgeRepository;
import com.sejong.projectservice.support.common.internal.UserExternalService;
import com.sejong.projectservice.support.common.internal.response.UserNameInfo;
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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
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
@DisplayName("cs 지식 통합 테스트")
public class CsKnowledgeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CsKnowledgeRepository csKnowledgeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @MockitoBean
    private UserExternalService userExternalService;

    @BeforeEach
    void setUp() {
        csKnowledgeRepository.deleteAll();
        categoryRepository.deleteAll();

        //UserExternalService 모킹 설정
        doNothing().when(userExternalService).validateExistence(any(String.class));
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
    @DisplayName("CS 지식을 생성할 수 있다.")
    void Cs_지식을_생성할_수_있다() throws Exception {
        //given
        CategoryEntity category = CategoryEntity.of("WEB-HACKING");
        CategoryEntity savedCategory = categoryRepository.save(category);

        CsKnowledgeReqDto request = new CsKnowledgeReqDto("CS 지식 제목", "CS 지식 내용", savedCategory.getName());

        //when && then
        mockMvc.perform(post("/cs-knowledge")
                        .header("X-User-Id", "tbntb-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("CS 지식 제목"))
                .andExpect(jsonPath("$.content").value("CS 지식 내용"))
                .andExpect(jsonPath("$.writerId").value("tbntb-1"))
                .andExpect(jsonPath("$.nickname").value("nickname-tbntb-1"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @DisplayName("존재하지 않는 카테고리로 cs 지식을 생성하려고 하면 에러가 발생한다")
    void 존재하지_않는_카테고리로_CS_지식을_생성하려고_하면_에러가_발생한다() throws Exception {
        //given
        CsKnowledgeReqDto request = new CsKnowledgeReqDto(
                "CS 지식 제목",
                "CS 지식 내용",
                "존재하지않는카테고리"
        );

        //when && then
        mockMvc.perform(post("/cs-knowledge")
                        .header("X-User-Id", "tbntb-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("CS 지식을 조회할 수 있다.")
    void CS_지식을_조회할_수_있다() throws Exception {
        //given
        CategoryEntity category = CategoryEntity.of("WEB-HACKING");
        CategoryEntity savedCategory = categoryRepository.save(category);

        CsKnowledgeEntity csKnowledge = createCsKnowledge("tbntb-1", savedCategory);
        CsKnowledgeEntity savedCsKnowledge = csKnowledgeRepository.save(csKnowledge);
        Long csKnowledgeId = savedCsKnowledge.getId();

        //when & then
        mockMvc.perform(get("/cs-knowledge/{csKnowledgeId}", csKnowledgeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(csKnowledgeId))
                .andExpect(jsonPath("$.title").value("CS 지식 제목"))
                .andExpect(jsonPath("$.content").value("CS 지식 내용"))
                .andExpect(jsonPath("$.category").value("WEB-HACKING"))
                .andExpect(jsonPath("$.writerId").value("tbntb-1"))
                .andExpect(jsonPath("$.nickname").value("nickname-tbntb-1"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @DisplayName("존재하지 않는 CS 지식을 조회하려고 하면 에러가 발생한다.")
    void 존재하지_않는_CS_지식을_조회하려고_하면_에러가_발생한다() throws Exception {
        //given
        Long nonExistentCsKnowledgeId = 999L;

        //when & then
        mockMvc.perform(get("/cs-knowledge/{csKnowledgeId}", nonExistentCsKnowledgeId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("CS 지식 소유주가 CS 지식을 수정할 수 있다.")
    void CS_지식_소유주가_CS_지식을_수정할_수_있다() throws Exception {
        //given
        CategoryEntity category = CategoryEntity.of("WEB-HACKING");
        CategoryEntity category2 = CategoryEntity.of("수정된 카테고리");
        CategoryEntity savedCategory = categoryRepository.save(category);
        CategoryEntity savedCategory2 = categoryRepository.save(category2);

        CsKnowledgeEntity csKnowledge = createCsKnowledge("tbntb-1", savedCategory);
        CsKnowledgeEntity savedCsKnowledge = csKnowledgeRepository.save(csKnowledge);
        Long csKnowledgeId = savedCsKnowledge.getId();

        CsKnowledgeReqDto updateRequest = new CsKnowledgeReqDto(
                "수정된 제목",
                "수정된 내용",
                "수정된 카테고리"
        );

        //when & then
        mockMvc.perform(put("/cs-knowledge/{csKnowledgeId}", csKnowledgeId)
                        .header("X-User-Id", "tbntb-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(csKnowledgeId))
                .andExpect(jsonPath("$.title").value("수정된 제목"))
                .andExpect(jsonPath("$.content").value("수정된 내용"))
                .andExpect(jsonPath("$.category").value("수정된 카테고리"));
    }

    @Test
    @DisplayName("CS 지식 소유자가 아닌 사용자가 수정하려고 하면 에러가 발생한다.")
    void CS_지식_소유주가_아닌_사용자가_수정하려고_하면_에러가_발생한다() throws Exception {
        //given
        CategoryEntity category = CategoryEntity.of("WEB-HACKING");
        CategoryEntity category2 = CategoryEntity.of("수정된 카테고리");
        CategoryEntity savedCategory = categoryRepository.save(category);
        CategoryEntity savedCategory2 = categoryRepository.save(category2);

        CsKnowledgeEntity csKnowledge = createCsKnowledge("tbntb-1", savedCategory);
        CsKnowledgeEntity savedCsKnowledge = csKnowledgeRepository.save(csKnowledge);
        Long csKnowledgeId = savedCsKnowledge.getId();

        CsKnowledgeReqDto updateRequest = new CsKnowledgeReqDto(
                "수정된 제목",
                "수정된 내용",
                "수정된 카테고리"
        );

        //when & then
        mockMvc.perform(put("/cs-knowledge/{csKnowledgeId}", csKnowledgeId)
                        .header("X-User-Id", "unauthorized-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("CS 지식 소유주가 CS 지식을 삭제할 수 있다.")
    void CS_지식_소유주가_CS_지식을_삭제할_수_있다() throws Exception {
        //given
        CategoryEntity category = CategoryEntity.of("WEB-HACKING");
        CategoryEntity savedCategory = categoryRepository.save(category);

        CsKnowledgeEntity csKnowledge = createCsKnowledge("tbntb-1", savedCategory);
        CsKnowledgeEntity savedCsKnowledge = csKnowledgeRepository.save(csKnowledge);
        Long csKnowledgeId = savedCsKnowledge.getId();

        //when & then
        mockMvc.perform(delete("/cs-knowledge/{csKnowledgeId}", csKnowledgeId)
                        .header("X-User-Id", "tbntb-1")
                        .header("X-User-Role", "USER"))
                .andExpect(status().isNoContent());

        // 삭제된 CS 지식이 조회되지 않는지 확인
        mockMvc.perform(get("/cs-knowledge/{csKnowledgeId}", csKnowledgeId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("관리자가 CS 지식을 삭제할 수 있다.")
    void 관리자가_CS_지식을_삭제할_수_있다() throws Exception {
        //given
        CategoryEntity category = CategoryEntity.of("WEB-HACKING");
        CategoryEntity savedCategory = categoryRepository.save(category);

        CsKnowledgeEntity csKnowledge = createCsKnowledge("tbntb-1", savedCategory);
        CsKnowledgeEntity savedCsKnowledge = csKnowledgeRepository.save(csKnowledge);
        Long csKnowledgeId = savedCsKnowledge.getId();

        //when && then
        mockMvc.perform(delete("/cs-knowledge/{csKnowledgeId}", csKnowledgeId)
                        .header("X-User-Id", "tbntb-999") //어드민 username이라 가정
                        .header("X-User-Role", "ADMIN"))
                .andExpect(status().isNoContent());

        // 삭제된 CS 지식이 조회되지 않는지 확인
        mockMvc.perform(get("/cs-knowledge/{csKnowledgeId}", csKnowledgeId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("CS 지식 소유자도 관리자도 아닌 사용자가 삭제하려고 하면 에러가 발생한다.")
    void CS_지식_소유자도_관리자도_아닌_사용자가_삭제하려고_하면_에러가_발생한다() throws Exception {
        //given
        CategoryEntity category = CategoryEntity.of("WEB-HACKING");
        CategoryEntity savedCategory = categoryRepository.save(category);

        CsKnowledgeEntity csKnowledge = createCsKnowledge("tbntb-1", savedCategory);
        CsKnowledgeEntity savedCsKnowledge = csKnowledgeRepository.save(csKnowledge);
        Long csKnowledgeId = savedCsKnowledge.getId();

        //when && then
        mockMvc.perform(delete("/cs-knowledge/{csKnowledgeId}", csKnowledgeId)
                        .header("X-User-Id", "unauthorized-user")
                        .header("X-User-Role", "USER"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("존재하지 않는 CS 지식을 삭제하려고 하면 에러가 발생한다.")
    void 존재하지_않는_CS_지식을_삭제하려고_하면_에러가_발생한다() throws Exception {
        //given
        Long nonExistentCsKnowledgeId = 999L;

        //when & then
        mockMvc.perform(delete("/cs-knowledge/{csKnowledgeId}", nonExistentCsKnowledgeId)
                        .header("X-User-Id", "tbntb-1")
                        .header("X-User-Role", "USER"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("카테고리별 CS 지식을 조회할 수 있다.")
    void 카테고리별_CS_지식을_조회할_수_있다() throws Exception {
        //given
        CategoryEntity category1 = CategoryEntity.of("백엔드");
        CategoryEntity category2 = CategoryEntity.of("프론트엔드");
        CategoryEntity savedCategory1 = categoryRepository.save(category1);
        CategoryEntity savedCategory2 = categoryRepository.save(category2);


        CsKnowledgeEntity csKnowledge1 = CsKnowledgeEntity.builder()
                .title("백엔드 CS 지식 1")
                .content("백엔드 내용 1")
                .writerId("tbntb-1")
                .categoryEntity(savedCategory1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        CsKnowledgeEntity csKnowledge2 = CsKnowledgeEntity.builder()
                .title("백엔드 CS 지식 2")
                .content("백엔드 내용 2")
                .writerId("tbntb-1")
                .categoryEntity(savedCategory1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        CsKnowledgeEntity csKnowledge3 = CsKnowledgeEntity.builder()
                .title("프론트엔드 CS 지식")
                .content("프론트엔드 내용")
                .writerId("tbntb-1")
                .categoryEntity(savedCategory2)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        csKnowledgeRepository.save(csKnowledge1);
        csKnowledgeRepository.save(csKnowledge2);
        csKnowledgeRepository.save(csKnowledge3);

        //when & then
        mockMvc.perform(get("/cs-knowledge/category/{categoryName}", "백엔드"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].category").value(Matchers.everyItem(Matchers.is("백엔드"))))
                .andExpect(jsonPath("$[*].title").value(Matchers.containsInAnyOrder("백엔드 CS 지식 1", "백엔드 CS 지식 2")));

    }

    @Test
    @DisplayName("CS 지식 존재 여부를 확인할 수 있다.")
    void CS_지식_존재_여부를_확인할_수_있다() throws Exception {
        //given
        CategoryEntity category = CategoryEntity.of("백엔드");
        CategoryEntity savedCategory = categoryRepository.save(category);

        CsKnowledgeEntity csKnowledge = createCsKnowledge("tbntb-1", savedCategory);
        CsKnowledgeEntity savedCsKnowledge = csKnowledgeRepository.save(csKnowledge);
        Long csKnowledgeId = savedCsKnowledge.getId();
        Long nonExistentCsKnowledgeId = 999L;

        //when & then - 존재하는 경우
        mockMvc.perform(get("/cs-knowledge/{csKnowledgeId}/exists", csKnowledgeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        //when & then - 존재하지 않는 경우
        mockMvc.perform(get("/cs-knowledge/{csKnowledgeId}/exists", nonExistentCsKnowledgeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));
    }

    @Test
    @DisplayName("CS 지식 생성 시 제목이 비어있으면 검증 에러가 발생한다.")
    void CS_지식_생성_시_제목이_비어있으면_검증_에러가_발생한다() throws Exception {
        //given
        CategoryEntity category = CategoryEntity.of("백엔드");
        CategoryEntity savedCategory = categoryRepository.save(category);

        CsKnowledgeReqDto request = new CsKnowledgeReqDto(
                "",
                "CS 지식 내용",
                savedCategory.getName()
        );

        //when & then
        mockMvc.perform(post("/cs-knowledge")
                        .header("X-User-Id", "tbntb-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("CS 지식 생성 시 내용이 비어있으면 검증 에러가 발생한다.")
    void CS_지식_생성_시_내용이_비어있으면_검증_에러가_발생한다() throws Exception {
        //given
        CategoryEntity category = CategoryEntity.of("백엔드");
        CategoryEntity savedCategory = categoryRepository.save(category);

        CsKnowledgeReqDto request = new CsKnowledgeReqDto(
                "CS 지식 제목",
                "",
                savedCategory.getName()
        );

        //when & then
        mockMvc.perform(post("/cs-knowledge")
                        .header("X-User-Id", "tbntb-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("CS 지식 생성 시 카테고리가 비어있으면 검증 에러가 발생한다.")
    void CS_지식_생성_시_카테고리가_비어있으면_검증_에러가_발생한다() throws Exception {
        //given
        CsKnowledgeReqDto request = new CsKnowledgeReqDto(
                "CS 지식 제목",
                "CS 지식 내용",
                null
        );

        //when & then
        mockMvc.perform(post("/cs-knowledge")
                        .header("X-User-Id", "tbntb-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("오프셋 페이지네이션으로 CS 지식 목록을 조회할 수 있다.")
    void 오프셋_페이지네이션으로_CS_지식_목록을_조회할_수_있다() throws Exception {
        //given
        CategoryEntity category = CategoryEntity.of("백엔드");
        CategoryEntity savedCategory = categoryRepository.save(category);

        // 10개의 CS 지식 생성
        for (int i = 1; i <= 10; i++) {
            CsKnowledgeEntity csKnowledge = CsKnowledgeEntity.builder()
                    .title("CS 지식 제목 " + i)
                    .content("CS 지식 내용 " + i)
                    .writerId("user-" + i)
                    .categoryEntity(savedCategory)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            csKnowledgeRepository.save(csKnowledge);
        }

        //when & then
        mockMvc.perform(get("/cs-knowledge/offset")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("sortDirection", "ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.totalPage").value(1))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(10))
                .andExpect(jsonPath("$.message").value("조회성공"));
    }

    @Test
    @DisplayName("커서 페이지네이션으로 CS 지식 목록을 조회하고 다음 페이지를 조회할 수 있다.")
    void 커서_페이지네이션으로_CS_지식_목록을_조회하고_다음_페이지를_조회할_수_있다() throws Exception {
        //given
        CategoryEntity category = CategoryEntity.of("백엔드");
        CategoryEntity savedCategory = categoryRepository.save(category);

        // 15개의 CS 지식 생성
        for (int i = 1; i <= 15; i++) {
            CsKnowledgeEntity csKnowledge = CsKnowledgeEntity.builder()
                    .title("CS 지식 제목 " + i)
                    .content("CS 지식 내용 " + i)
                    .writerId("tbntb-" + i)
                    .categoryEntity(savedCategory)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            csKnowledgeRepository.save(csKnowledge);
        }

        //when & then - 첫 페이지 조회 (size=10)
        String firstPageResponse = mockMvc.perform(get("/cs-knowledge/cursor")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("sortDirection", "DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.hasNext").value(true))
                .andExpect(jsonPath("$.nextCursorId").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // JSON에서 nextCursorId 추출
        JsonNode jsonNode = objectMapper.readTree(firstPageResponse);
        Long nextCursorId = jsonNode.get("nextCursorId").asLong();

        //when & then - 다음 페이지 조회
        mockMvc.perform(get("/cs-knowledge/cursor")
                        .param("cursor.projectId", String.valueOf(nextCursorId))
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("sortDirection", "DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.nextCursorId").doesNotExist());
    }

    @Test
    @DisplayName("오프셋 페이지네이션과 커서 페이지네이션에서 오름차순/내림차순 정렬이 제대로 작동한다.")
    void 오프셋_페이지네이션과_커서_페이지네이션에서_오름차순_내림차순_정렬이_제대로_작동한다() throws Exception {
        //given
        CategoryEntity category = CategoryEntity.of("백엔드");
        CategoryEntity savedCategory = categoryRepository.save(category);

        for (int i = 1; i <= 5; i++) {
            CsKnowledgeEntity csKnowledge = CsKnowledgeEntity.builder()
                    .title("CS 지식 제목 " + i)
                    .content("CS 지식 내용 " + i)
                    .writerId("user-" + i)
                    .categoryEntity(savedCategory)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            csKnowledgeRepository.save(csKnowledge);
        }

        //when & then - 오프셋 페이지네이션 오름차순 정렬
        String offsetAscResponse = mockMvc.perform(get("/cs-knowledge/offset")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "id")
                        .param("sortDirection", "ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(5))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode offsetAscNode = objectMapper.readTree(offsetAscResponse);
        JsonNode offsetAscData = offsetAscNode.get("data");
        Long offsetAscFirstId = offsetAscData.get(0).get("id").asLong();
        Long offsetAscLastId = offsetAscData.get(4).get("id").asLong();
        assertTrue(offsetAscFirstId < offsetAscLastId,
                "오프셋 페이지네이션 오름차순 정렬 실패. 첫 번째 ID: " + offsetAscFirstId + ", 마지막 ID: " + offsetAscLastId);

        //when & then - 오프셋 페이지네이션 내림차순 정렬
        String offsetDescResponse = mockMvc.perform(get("/cs-knowledge/offset")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "id")
                        .param("sortDirection", "DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(5))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode offsetDescNode = objectMapper.readTree(offsetDescResponse);
        JsonNode offsetDescData = offsetDescNode.get("data");
        Long offsetDescFirstId = offsetDescData.get(0).get("id").asLong();
        Long offsetDescLastId = offsetDescData.get(4).get("id").asLong();
        assertTrue(offsetDescFirstId > offsetDescLastId,
                "오프셋 페이지네이션 내림차순 정렬 실패. 첫 번째 ID: " + offsetDescFirstId + ", 마지막 ID: " + offsetDescLastId);

        //when & then - 커서 페이지네이션 오름차순 정렬
        String cursorAscResponse = mockMvc.perform(get("/cs-knowledge/cursor")
                        .param("size", "5")
                        .param("sortBy", "id")
                        .param("sortDirection", "ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(5))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode cursorAscNode = objectMapper.readTree(cursorAscResponse);
        JsonNode cursorAscContent = cursorAscNode.get("content");
        Long cursorAscFirstId = cursorAscContent.get(0).get("id").asLong();
        Long cursorAscLastId = cursorAscContent.get(4).get("id").asLong();
        assertTrue(cursorAscFirstId < cursorAscLastId,
                "커서 페이지네이션 오름차순 정렬 실패. 첫 번째 ID: " + cursorAscFirstId + ", 마지막 ID: " + cursorAscLastId);

        //when & then - 커서 페이지네이션 내림차순 정렬
        String cursorDescResponse = mockMvc.perform(get("/cs-knowledge/cursor")
                        .param("size", "5")
                        .param("sortBy", "id")
                        .param("sortDirection", "DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(5))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode cursorDescNode = objectMapper.readTree(cursorDescResponse);
        JsonNode cursorDescContent = cursorDescNode.get("content");
        Long cursorDescFirstId = cursorDescContent.get(0).get("id").asLong();
        Long cursorDescLastId = cursorDescContent.get(4).get("id").asLong();
        assertTrue(cursorDescFirstId > cursorDescLastId,
                "커서 페이지네이션 내림차순 정렬 실패. 첫 번째 ID: " + cursorDescFirstId + ", 마지막 ID: " + cursorDescLastId);
    }

    private CsKnowledgeEntity createCsKnowledge(String username, CategoryEntity savedCategory) {
        return CsKnowledgeEntity.builder()
                .title("CS 지식 제목")
                .content("CS 지식 내용")
                .writerId(username)
                .categoryEntity(savedCategory)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
