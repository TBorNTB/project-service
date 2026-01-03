package com.sejong.projectservice.domains.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sejong.projectservice.domains.category.domain.CategoryEntity;
import com.sejong.projectservice.domains.category.dto.CategoryAddRequest;
import com.sejong.projectservice.domains.category.dto.CategoryDeleteRequest;
import com.sejong.projectservice.domains.category.dto.CategoryDescriptionRequest;
import com.sejong.projectservice.domains.category.dto.CategoryUpdateRequest;
import com.sejong.projectservice.domains.category.repository.CategoryRepository;
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
import org.hamcrest.Matchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("카테고리 통합 테스트")
public class CategoryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() { categoryRepository.deleteAll();}

    @Test
    @DisplayName("관리자가 카테고리를 생성할 수 있다.")
    void 관리자가_카테고리를_생성할_수_있다() throws Exception{
        // given
        CategoryAddRequest request = CategoryAddRequest.builder()
                .name("백엔드")
                .build();

        // when & then
        mockMvc.perform(post("/api/category")
                .header("X-User-Role", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("백엔드"))
                .andExpect(jsonPath("$.message").value("카테고리 생성 완료"))
                .andExpect(jsonPath("$.id").exists());


    }

    @Test
    @DisplayName("일반 사용자는 카테고리를 생성할 수 없다")
    void 일반_사용자는_카테고리를_생성할_수_없다() throws Exception{
        //given
        CategoryAddRequest request = CategoryAddRequest.builder()
                .name("백엔드")
                .build();

        //when && then
        mockMvc.perform(post("/api/category")
                .header("X-User-Role", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("어드민만 가능한 요청입니다."));
    }

    @Test
    @DisplayName("카테고리 전체를 조회할 수 있다.")
    void 카테고리_전체를_조회할_수_있다() throws Exception{
        //given - Repository를 통해 직접 데이터 삽입 (조회 기능 테스트에 집중)
        CategoryEntity category1 = CategoryEntity.of("백엔드");
        CategoryEntity category2 = CategoryEntity.of("프론트엔드");
        categoryRepository.save(category1);
        categoryRepository.save(category2);

        //when & then
        mockMvc.perform(get("/api/category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories").isArray())
                .andExpect(jsonPath("$.categories.length()").value(2))
                .andExpect(jsonPath("$.categories[*].name").value(Matchers.containsInAnyOrder("백엔드", "프론트엔드")));
    }

    @Test
    @DisplayName("관리자가 카테고리 이름을 수정할 수 있다.")
    void 관리자가_카테고리_이름을_수정할_수_있다() throws Exception {
        // given
        CategoryEntity category1 = CategoryEntity.of("백엔드");
        categoryRepository.save(category1);

        // when
        CategoryUpdateRequest updateRequest = CategoryUpdateRequest.builder()
                .prevName("백엔드")
                .nextName("Backend")
                .build();

        // then

        mockMvc.perform(put("/api/category")
                .header("X-User-Role", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Backend"))
                .andExpect(jsonPath("$.message").value("카테고리 수정 완료"));

        // 수정된 카테고리가 조회되는지 확인
        mockMvc.perform(get("/api/category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories[0].name").value("Backend"));
    }

    @Test
    @DisplayName("관리자가 카테고리 설명을 추가할 수 있다")
    void 관리자가_카테고리_설명을_추가할_수_있다() throws Exception {
        // given
        CategoryEntity categoryEntity = CategoryEntity.of("백엔드");
        CategoryEntity savedEntity = categoryRepository.save(categoryEntity);
        Long categoryId = savedEntity.getId();

        // when
        CategoryDescriptionRequest descriptionRequest = CategoryDescriptionRequest.builder()
                .description("백엔드 개발 관련 카테고리입니다.")
                .build();

        // then
        mockMvc.perform(patch("/api/category/description/{categoryId}",categoryId)
                .header("X-User-Role", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(descriptionRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("백엔드"))
                .andExpect(jsonPath("$.message").value("카테고리 수정 완료"));

        // 설명이 추가되었는지 확인
        mockMvc.perform(get("/api/category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories[0].description").value("백엔드 개발 관련 카테고리입니다."));
    }

    @Test
    @DisplayName("관리자가 카테고리를 삭제할 수 있다.")
    void 관리자가_카테고리를_삭제할_수_있다() throws Exception {
        // given
        categoryRepository.save(CategoryEntity.of("백엔드"));

        // when
        CategoryDeleteRequest deleteRequest = CategoryDeleteRequest.builder()
                .name("백엔드")
                .build();

        // then
        mockMvc.perform(delete("/api/category")
                .header("X-User-Role", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("백엔드"))
                .andExpect(jsonPath("$.message").value("카테고리 삭제 완료"));

        // 삭제된 카테고리가 조회되지 않는지 확인
        mockMvc.perform(get("/api/category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories.length()").value(0));

    }

    @Test
    @DisplayName("존재하지 않는 카테고리를 삭제하려고 하면 에러가 발생한다")
    void 존재하지_않는_카테고리를_삭제하려고_하면_에러가_발생한다() throws Exception{
        //given
        CategoryDeleteRequest deleteRequest = CategoryDeleteRequest.builder()
                .name("존재하지않는카테고리")
                .build();

        //when && then
        mockMvc.perform(delete("/api/category")
                .header("X-User-Role", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("카테고리 생성 시 이름이 비어있으면 검증 에러가 발생한다")
    void 카테고리_생성_시_이름이_비어있으면_검증_에러가_발생한다() throws Exception {
        // given
        CategoryAddRequest request = CategoryAddRequest.builder()
                .name("")
                .build();

        // when & then
        mockMvc.perform(post("/api/category")
                        .header("X-User-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
