package com.sejong.projectservice.domains.techstack;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sejong.projectservice.domains.techstack.domain.TechStackEntity;
import com.sejong.projectservice.domains.techstack.dto.TechStackCreateReq;
import com.sejong.projectservice.domains.techstack.repository.TechStackRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("테크스택 통합 테스트")
public class TechStackIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TechStackRepository techStackRepository;

    @BeforeEach
    void setUp() {
        techStackRepository.deleteAll();
    }

    @Test
    @DisplayName("관리자가 테크스택을 생성할 수 있다.")
    void 관리자가_테크스택을_생성할_수_있다() throws Exception {
        //given
        TechStackCreateReq request = new TechStackCreateReq();
        request.setName("Spring Boot");

        //when && then
        mockMvc.perform(post("/api/tech-stack")
                        .header("X-User-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Spring Boot"));
    }

    @Test
    @DisplayName("일반 사용자가 테크스택을 생성하려고 하면 에러가 발생한다.")
    void 일반_사용자가_테크스택을_생성하려고_하면_에러가_발생한다() throws Exception {
        //given
        TechStackCreateReq request = new TechStackCreateReq();
        request.setName("Spring Boot");

        //when && then
        mockMvc.perform(post("/api/tech-stack")
                        .header("X-User-Role", "USER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("테크스택을 조회할 수 있다.")
    void 테크스택을_조회할_수_있다() throws Exception {
        //given
        TechStackEntity techstack = TechStackEntity.of("Spring Boot");
        TechStackEntity savedTechStack = techStackRepository.save(techstack);
        Long techStackId = savedTechStack.getId();

        //when && then
        mockMvc.perform(get("/api/tech-stack/{techStackId}", techStackId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(techStackId))
                .andExpect(jsonPath("$.name").value("Spring Boot"));
    }

    @Test
    @DisplayName("존재하지 않는 테크스택을 조회하려고 하면 에러가 발생한다.")
    void 존재하지_않는_테크스택을_조회하려고_하면_에러가_발생한다() throws Exception {
        //given
        Long nonExistentTechStackId = 999L;

        //when && then
        mockMvc.perform(get("/api/tech-stack/{techStackId}", nonExistentTechStackId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("관리자가 테크스택을 수정할 수 있다.")
    void 관리자가_테크스택을_수정할_수_있다() throws Exception {
        //given
        TechStackEntity techStack = TechStackEntity.of("Spring Boot");
        TechStackEntity savedTechStack = techStackRepository.save(techStack);
        Long techStackId = savedTechStack.getId();

        TechStackCreateReq updateRequest = new TechStackCreateReq();
        updateRequest.setName("Docker");

        //when && then
        mockMvc.perform(put("/api/tech-stack/{techStackId}", techStackId)
                        .header("X-User-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(techStackId))
                .andExpect(jsonPath("$.name").value("Docker"));
    }

    @Test
    @DisplayName("일반 사용자가 테크스택을 수정하려고 하면 에러가 발생한다.")
    void 일반_사용자가_테크스택을_수정하려고_하면_에러가_발생한다() throws Exception {
        //given
        TechStackEntity techStack = TechStackEntity.of("Spring Boot");
        TechStackEntity savedTechStack = techStackRepository.save(techStack);
        Long techStackId = savedTechStack.getId();

        TechStackCreateReq updateRequest = new TechStackCreateReq();
        updateRequest.setName("Docker");

        //when && then
        mockMvc.perform(put("/api/tech-stack/{techStackId}", techStackId)
                        .header("X-User-Role", "USER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("관리자가 테크스택을 삭제할 수 있다.")
    void 관리자가_테크스택을_삭제할_수_있다() throws Exception {
        //given
        TechStackEntity techStack = TechStackEntity.of("Spring Boot");
        TechStackEntity savedTechStack = techStackRepository.save(techStack);
        Long techStackId = savedTechStack.getId();

        //when && then
        mockMvc.perform(delete("/api/tech-stack/{techStackId}", techStackId)
                        .header("X-User-Role", "ADMIN"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("일반 사용자가 테크스택을 삭제하려고 하면 에러가 발생한다")
    void 일반_사용자가_태크스택을_삭제하려고_하면_에러가_발생한다() throws Exception {
        //given
        TechStackEntity techStack = TechStackEntity.of("Spring Boot");
        TechStackEntity savedTechStack = techStackRepository.save(techStack);
        Long techStackId = savedTechStack.getId();

        //when && then
        mockMvc.perform(delete("/api/tech-stack/{techStackId}", techStackId)
                        .header("X-User-Role", "USER"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("존재하지 않는 테크스택을 삭제하려고 하면 에러가 발생한다.")
    void 존재하지_않는_테크스택을_삭제하려고_하면_에러가_발생한다() throws Exception {
        //given
        Long nonExistentTechStackId = 999L;

        //when && then
        mockMvc.perform(delete("/api/tech-stack/{techStackId}", nonExistentTechStackId)
                        .header("X-User-Role", "ADMIN"))
                .andExpect(status().isNotFound());
    }
}
