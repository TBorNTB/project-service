package com.sejong.projectservice.domains.qna;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sejong.projectservice.domains.category.domain.CategoryEntity;
import com.sejong.projectservice.domains.category.repository.CategoryRepository;
import com.sejong.projectservice.domains.qna.domain.QuestionAnswerEntity;
import com.sejong.projectservice.domains.qna.domain.QuestionEntity;
import com.sejong.projectservice.domains.qna.dto.request.QuestionCreateRequest;
import com.sejong.projectservice.domains.qna.dto.request.QuestionUpdateRequest;
import com.sejong.projectservice.domains.qna.repository.QuestionAnswerRepository;
import com.sejong.projectservice.domains.qna.repository.QuestionRepository;
import com.sejong.projectservice.support.common.internal.UserExternalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("QnA API 통합 테스트")
public class QnaApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionAnswerRepository questionAnswerRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @MockBean
    private UserExternalService userExternalService;

    @BeforeEach
    void setUp() {
        when(userExternalService.getUserNameInfos(anyList())).thenReturn(Map.of());
        doNothing().when(userExternalService).validateExistence(anyString());

        questionAnswerRepository.deleteAll();
        questionRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    @DisplayName("질문 생성 API가 성공하고 status=NOT_ACCEPTED로 내려온다")
    void create_question_should_succeed() throws Exception {
        CategoryEntity web = categoryRepository.save(CategoryEntity.of("Web Hacking"));

        QuestionCreateRequest request = QuestionCreateRequest.builder()
                .title("t1")
                .description("d1")
                .content("c1")
                .categories(List.of(web.getName()))
                .build();

        mockMvc.perform(post("/api/question")
                        .header("X-User-Id", "user1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("t1"))
                .andExpect(jsonPath("$.status").value("NOT_ACCEPTED"));
    }

    @Test
    @DisplayName("카테고리가 동일해도 수정이 500 없이 성공한다 (uk_question_category 중복 방지)")
    void update_with_same_categories_should_succeed() throws Exception {
        CategoryEntity web = categoryRepository.save(CategoryEntity.of("Web Hacking"));
        QuestionEntity question = questionRepository.save(
                QuestionEntity.of("t1", "d1", "c1", "user1", List.of(web))
        );

        QuestionUpdateRequest request = QuestionUpdateRequest.builder()
                .title("t2")
                .description("d2")
                .content("c2")
                .categories(List.of(web.getName()))
                .build();

        mockMvc.perform(put("/api/question/{questionId}", question.getId())
                        .header("X-User-Id", "user1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(question.getId().intValue()))
                .andExpect(jsonPath("$.title").value("t2"));
    }

    @Test
    @DisplayName("status=ANSWERED면 답변이 있는 질문만 조회된다")
    void statusAnswered_filters_only_answered_questions() throws Exception {
        CategoryEntity web = categoryRepository.save(CategoryEntity.of("Web Hacking"));

        QuestionEntity q1 = questionRepository.save(
                QuestionEntity.of("RSA 취약점", "설명", "내용", "user1", List.of(web))
        );
        questionRepository.save(
                QuestionEntity.of("IDA 디버깅", "설명", "내용", "user2", List.of(web))
        );

        questionAnswerRepository.save(QuestionAnswerEntity.of("답변", "user3", q1));

        mockMvc.perform(get("/api/question/offset/search")
                        .param("status", "ANSWERED")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "createdAt")
                        .param("sortDirection", "DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(q1.getId().intValue()))
                .andExpect(jsonPath("$.data[0].answerCount").value(1));
    }

    @Test
    @DisplayName("status=ACCEPTED면 채택 완료 질문만 조회된다")
    void statusAccepted_filters_only_accepted_questions() throws Exception {
        CategoryEntity rev = categoryRepository.save(CategoryEntity.of("Reversing"));

        QuestionEntity q1 = questionRepository.save(
                QuestionEntity.of("질문1", "설명", "내용", "user1", List.of(rev))
        );
        questionRepository.save(
                QuestionEntity.of("질문2", "설명", "내용", "user2", List.of(rev))
        );

        q1.markAccepted();
        questionRepository.save(q1);

        mockMvc.perform(get("/api/question/offset/search")
                        .param("status", "ACCEPTED")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "createdAt")
                        .param("sortDirection", "DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(q1.getId().intValue()));
    }

    @Test
    @DisplayName("categoryNames로 기술 태그(카테고리) 필터링된다")
    void categoryNames_filters_questions() throws Exception {
        CategoryEntity web = categoryRepository.save(CategoryEntity.of("Web Hacking"));
        CategoryEntity ctf = categoryRepository.save(CategoryEntity.of("CTF"));

        QuestionEntity q1 = questionRepository.save(
                QuestionEntity.of("질문1", "설명", "내용", "user1", List.of(web))
        );
        questionRepository.save(
                QuestionEntity.of("질문2", "설명", "내용", "user2", List.of(ctf))
        );

        mockMvc.perform(get("/api/question/offset/search")
                        .param("categoryNames", web.getName())
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "createdAt")
                        .param("sortDirection", "DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(q1.getId().intValue()));
    }

    @Test
    @DisplayName("keyword로 제목/설명/본문 검색된다")
    void keyword_searches_title_description_content() throws Exception {
        CategoryEntity web = categoryRepository.save(CategoryEntity.of("Web Hacking"));

        QuestionEntity q1 = questionRepository.save(
                QuestionEntity.of("Buffer Overflow", "설명", "내용", "user1", List.of(web))
        );
        questionRepository.save(
                QuestionEntity.of("질문2", "설명", "IDA Pro", "user2", List.of(web))
        );

        mockMvc.perform(get("/api/question/offset/search")
                        .param("keyword", "buffer")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "createdAt")
                        .param("sortDirection", "DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(q1.getId().intValue()));
    }

    @Test
    @DisplayName("답변 생성/조회/수정 API가 정상 동작한다")
    void answer_create_get_update_should_work() throws Exception {
        QuestionEntity question = questionRepository.save(
                QuestionEntity.of("t1", "d1", "c1", "qOwner", List.of())
        );

        String createResponse = mockMvc.perform(post("/api/question/{questionId}/answer", question.getId())
                        .header("X-User-Id", "aUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"answer1\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.questionId").value(question.getId().intValue()))
                .andExpect(jsonPath("$.accepted").value(false))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long answerId = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(get("/api/question/answer/{answerId}", answerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value((int) answerId))
                .andExpect(jsonPath("$.content").value("answer1"));

        mockMvc.perform(put("/api/question/answer/{answerId}", answerId)
                        .header("X-User-Id", "aUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"answer2\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value((int) answerId))
                .andExpect(jsonPath("$.content").value("answer2"));
    }

    @Test
    @DisplayName("답변 목록(오프셋) API가 정상 동작한다")
    void answer_offset_list_should_work() throws Exception {
        QuestionEntity question = questionRepository.save(
                QuestionEntity.of("t1", "d1", "c1", "qOwner", List.of())
        );
        questionAnswerRepository.save(QuestionAnswerEntity.of("a1", "u1", question));
        questionAnswerRepository.save(QuestionAnswerEntity.of("a2", "u2", question));

        mockMvc.perform(get("/api/question/{questionId}/answer/offset", question.getId())
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("sortDirection", "ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @DisplayName("채택된 답변을 삭제하면 질문 상태가 NOT_ACCEPTED로 되돌아간다")
    void delete_accepted_answer_should_unaccept_question() throws Exception {
        String questionOwner = "qOwner";
        String answerOwner = "aUser";

        QuestionEntity question = questionRepository.save(
                QuestionEntity.of("t1", "d1", "c1", questionOwner, List.of())
        );

        String createResponse = mockMvc.perform(post("/api/question/{questionId}/answer", question.getId())
                        .header("X-User-Id", answerOwner)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"answer1\"}"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(createResponse);
        long answerId = json.get("id").asLong();

        mockMvc.perform(post("/api/question/{questionId}/answer/{answerId}/accept", question.getId(), answerId)
                        .header("X-User-Id", questionOwner))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/question/{questionId}", question.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));

        mockMvc.perform(delete("/api/question/answer/{answerId}", answerId)
                        .header("X-User-Id", answerOwner))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/question/{questionId}", question.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("NOT_ACCEPTED"));
    }

    @Test
    @DisplayName("답변 채택 토글 API가 질문 status를 ACCEPTED로 만든다")
    void accept_toggle_should_accept_question() throws Exception {
        String questionOwner = "qOwner";
        String answerOwner = "aUser";

        QuestionEntity question = questionRepository.save(
                QuestionEntity.of("t1", "d1", "c1", questionOwner, List.of())
        );

        String createResponse = mockMvc.perform(post("/api/question/{questionId}/answer", question.getId())
                        .header("X-User-Id", answerOwner)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"answer1\"}"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long answerId = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(post("/api/question/{questionId}/answer/{answerId}/accept", question.getId(), answerId)
                        .header("X-User-Id", questionOwner))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/question/{questionId}", question.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));

        mockMvc.perform(get("/api/question/answer/{answerId}", answerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accepted").value(true));
    }

    @Test
    @DisplayName("답변 삭제는 작성자만 가능하다")
    void delete_answer_forbidden_for_non_owner() throws Exception {
        QuestionEntity question = questionRepository.save(
                QuestionEntity.of("t1", "d1", "c1", "qOwner", List.of())
        );
        QuestionAnswerEntity answer = questionAnswerRepository.save(
                QuestionAnswerEntity.of("answer1", "aUser", question)
        );

        mockMvc.perform(delete("/api/question/answer/{answerId}", answer.getId())
                        .header("X-User-Id", "otherUser"))
                .andExpect(status().isForbidden());
    }
}
