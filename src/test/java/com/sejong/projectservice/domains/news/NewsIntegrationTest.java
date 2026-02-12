package com.sejong.projectservice.domains.news;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sejong.projectservice.domains.news.domain.ContentEmbeddable;
import com.sejong.projectservice.domains.news.domain.NewsEntity;
import com.sejong.projectservice.domains.news.dto.NewsReqDto;
import com.sejong.projectservice.domains.news.repository.NewsRepository;
import com.sejong.projectservice.support.common.constants.NewsCategory;
import com.sejong.projectservice.support.common.internal.UserExternalService;
import com.sejong.projectservice.support.common.internal.response.UserNameInfo;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
@DisplayName("뉴스 통합 테스트")
public class NewsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NewsRepository newsRepository;

    @MockitoBean
    private UserExternalService userExternalService;

    @BeforeEach
    void setUp() {
        newsRepository.deleteAll();

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
    @DisplayName("뉴스를 생성할 수 있다.")
    void 뉴스를_생성할_수_있다() throws Exception {
        //given
        NewsReqDto request = NewsReqDto.builder()
                .title("뉴스 제목")
                .summary("뉴스 요약")
                .content("뉴스 내용")
                .category("MT")
                .participantIds(List.of("tbntb-2", "tbntb-3"))
                .tags(List.of("tag1", "tag2"))
                .build();

        //when && then
        mockMvc.perform(post("/news")
                        .header("X-User-Id", "tbntb-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("뉴스 제목"))
                .andExpect(jsonPath("$.summary").value("뉴스 요약"))
                .andExpect(jsonPath("$.content").value("뉴스 내용"))
                .andExpect(jsonPath("$.category").value("MT"))
                .andExpect(jsonPath("$.writerId").value("tbntb-1"))
                .andExpect(jsonPath("$.writerNickname").value("nickname-tbntb-1"))
                .andExpect(jsonPath("$.participantIds").isArray())
                .andExpect(jsonPath("$.participantIds.length()").value(2))
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.tags.length()").value(2))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @DisplayName("뉴스를 조회할 수 있다.")
    void 뉴스를_조회할_수_있다() throws Exception {
        //given
        NewsEntity news = createNews("tbntb-1", "뉴스 제목", "뉴스 요약", "뉴스 내용", NewsCategory.MT);
        NewsEntity savedNews = newsRepository.save(news);
        Long newsId = savedNews.getId();

        //when && then
        mockMvc.perform(get("/news/{newsId}", newsId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(newsId))
                .andExpect(jsonPath("$.title").value("뉴스 제목"))
                .andExpect(jsonPath("$.summary").value("뉴스 요약"))
                .andExpect(jsonPath("$.content").value("뉴스 내용"))
                .andExpect(jsonPath("$.category").value("MT"))
                .andExpect(jsonPath("$.writerId").value("tbntb-1"))
                .andExpect(jsonPath("$.writerNickname").value("nickname-tbntb-1"));
    }

    @Test
    @DisplayName("존재하지 않는 뉴스를 조회하려고 하면 에러가 발생한다.")
    void 존재하지_않는_뉴스를_조회하려고_하면_에러가_발생한다() throws Exception {
        //given
        Long nonExistentNewsId = 999L;

        //when & then
        mockMvc.perform(get("/news/{newsId}", nonExistentNewsId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("뉴스 소유자가 뉴스를 수정할 수 있다.")
    void 뉴스_소유자가_뉴스를_수정할_수_있다() throws Exception {
        //given
        NewsEntity news = createNews("tbntb-1", "뉴스 제목", "뉴스 요약", "뉴스 내용", NewsCategory.MT);
        NewsEntity savedNews = newsRepository.save(news);
        Long newsId = savedNews.getId();

        NewsReqDto request = NewsReqDto.builder()
                .title("수정된 제목")
                .summary("수정된 요약")
                .content("수정된 내용")
                .category("MT")
                .participantIds(List.of("tbntb-2", "tbntb-3"))
                .tags(List.of("tag1", "tag2"))
                .build();

        //when & then
        mockMvc.perform(put("/news/{newsId}", newsId)
                        .header("X-User-Id", "tbntb-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(newsId))
                .andExpect(jsonPath("$.title").value("수정된 제목"))
                .andExpect(jsonPath("$.summary").value("수정된 요약"))
                .andExpect(jsonPath("$.content").value("수정된 내용"))
                .andExpect(jsonPath("$.category").value("MT"));
    }

    @Test
    @DisplayName("소유주가 아닌 사용자가 뉴스를 수정하려고 하면 에러가 발생한다.")
    void 소유주가_아닌_사용자가_뉴스를_수정하려고_하면_에러가_발생한다() throws Exception {
        //given
        NewsEntity news = createNews("tbntb-1", "뉴스 제목", "뉴스 요약", "뉴스 내용", NewsCategory.MT);
        NewsEntity savedNews = newsRepository.save(news);
        Long newsId = savedNews.getId();

        NewsReqDto request = NewsReqDto.builder()
                .title("수정된 제목")
                .summary("수정된 요약")
                .content("수정된 내용")
                .category("MT")
                .participantIds(List.of("tbntb-2", "tbntb-3"))
                .tags(List.of("tag1", "tag2"))
                .build();

        //when && then
        mockMvc.perform(put("/news/{newsId}", newsId)
                        .header("X-User-Id", "unauthorized-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("뉴스 소유자가 뉴스를 삭제할 수 있다.")
    void 뉴스_소유자가_뉴스를_삭제할_수_있다() throws Exception {
        //given
        NewsEntity news = createNews("tbntb-1", "뉴스 제목", "뉴스 요약", "뉴스 내용", NewsCategory.MT);
        NewsEntity savedNews = newsRepository.save(news);
        Long newsId = savedNews.getId();

        //when & then
        mockMvc.perform(delete("/news/{newsId}", newsId)
                        .header("X-User-Id", "tbntb-1"))
                .andExpect(status().isOk());

        // 삭제된 뉴스가 조회되지 않는지 확인
        mockMvc.perform(get("/news/{newsId}", newsId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("소유주가 아닌 사용자가 뉴스를 삭제하려고 하면 에러가 발생한다.")
    void 소유자가_아닌_사용자가_뉴스를_삭제하려고_하면_에러가_발생한다() throws Exception {
        //given
        NewsEntity news = createNews("tbntb-1", "뉴스 제목", "뉴스 요약", "뉴스 내용", NewsCategory.MT);
        NewsEntity savedNews = newsRepository.save(news);
        Long newsId = savedNews.getId();

        //when & then
        mockMvc.perform(delete("/news/{newsId}", newsId)
                        .header("X-User-Id", "unauthorized-user"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("존재하지 않는 뉴스를 삭제하려고 하면 에러가 발생한다.")
    void 존재하지_않는_뉴스를_삭제하려고_하면_에러가_발생한다() throws Exception {
        //given
        Long nonExistentNewsId = 999L;

        //when & then
        mockMvc.perform(delete("/news/{newsId}", nonExistentNewsId)
                        .header("X-User-Id", "tbntb-1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("오프셋 페이지네이션으로 뉴스 목록을 조회할 수 있다.")
    void 오프셋_페이지네이션으로_뉴스_목록을_조회할_수_있다() throws Exception {
        //given
        for (int i = 1; i <= 10; i++) {
            NewsEntity news = createNews("tbntb-" + i, "뉴스 제목 " + i, "뉴스 요약 " + i, "뉴스 내용 " + i, NewsCategory.MT);
            newsRepository.save(news);
        }

        //when & then
        mockMvc.perform(get("/news/offset")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "id")
                        .param("sortDirection", "ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.totalPage").exists())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(5))
                .andExpect(jsonPath("$.message").value("조회성공"));
    }

    @Test
    @DisplayName("커서 페이지네이션으로 뉴스 목록을 조회하고 다음 페이지를 조회할 수 있다.")
    void 커서_페이지네이션으로_뉴스_목록을_조회하고_다음_페이지를_조회할_수_있다() throws Exception {
        //given
        for (int i = 1; i <= 15; i++) {
            NewsEntity news = createNews("tbntb-" + i, "뉴스 제목 " + i, "뉴스 요약 " + i, "뉴스 내용 " + i, NewsCategory.MT);
            newsRepository.save(news);
        }

        //when & then - 첫 페이지 조회 (size=10)
        String firstPageResponse = mockMvc.perform(get("/news/cursor")
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

        JsonNode jsonNode = objectMapper.readTree(firstPageResponse);
        Long nextCursorId = jsonNode.get("nextCursorId").asLong();

        //when & then - 다음 페이지 조회
        mockMvc.perform(get("/news/cursor")
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
        for (int i = 1; i <= 5; i++) {
            NewsEntity news = createNews("tbntb-" + i, "뉴스 제목 " + i, "뉴스 요약 " + i, "뉴스 내용 " + i, NewsCategory.MT);
            newsRepository.save(news);
        }

        //when & then - 오프셋 페이지네이션 오름차순 정렬
        String offsetAscResponse = mockMvc.perform(get("/news/offset")
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
        String offsetDescResponse = mockMvc.perform(get("/news/offset")
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
        String cursorAscResponse = mockMvc.perform(get("/news/cursor")
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
        String cursorDescResponse = mockMvc.perform(get("/news/cursor")
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

    @Test
    @DisplayName("뉴스 생성 시 존재하지 않는 카테고리로 생성하려고 하면 에러가 발생한다.")
    void 뉴스_생성_시_존재하지_않는_카테고리로_생성하려고_하면_에러가_발생한다() throws Exception {
        //given
        NewsReqDto request = NewsReqDto.builder()
                .title("뉴스 제목")
                .summary("뉴스 요약")
                .content("뉴스 내용")
                .category("존재하지 않는 카테고리")
                .participantIds(List.of("tbntb-2", "tbntb-3"))
                .tags(List.of("tag1", "tag2"))
                .build();
        //when & then
        mockMvc.perform(post("/news")
                        .header("X-User-Id", "tbntb-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    private NewsEntity createNews(String writerId, String title, String summary, String content,
                                  NewsCategory category) {
        ContentEmbeddable contentEmbeddable = ContentEmbeddable.of(
                com.sejong.projectservice.domains.news.domain.Content.of(title, summary, content, category)
        );

        return NewsEntity.builder()
                .content(contentEmbeddable)
                .thumbnailPath(null)
                .writerId(writerId)
                .participantIds(null)
                .tags(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
