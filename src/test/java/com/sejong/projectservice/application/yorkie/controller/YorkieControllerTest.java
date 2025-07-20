package com.sejong.projectservice.application.yorkie.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sejong.projectservice.application.project.config.MockBeansConfig;
import com.sejong.projectservice.application.project.controller.ProjectController;
import com.sejong.projectservice.application.project.service.ProjectService;
import com.sejong.projectservice.application.yorkie.controller.fixture.YorkieFixture;
import com.sejong.projectservice.application.yorkie.dto.request.YorkieRegisterRequest;
import com.sejong.projectservice.application.yorkie.dto.response.YorkieRegisterResponse;
import com.sejong.projectservice.application.yorkie.dto.response.YorkieSearchResponse;
import com.sejong.projectservice.application.yorkie.service.YorkieService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(MockBeansConfig.class)
@AutoConfigureMockMvc
@WebMvcTest(YorkieController.class)
@ActiveProfiles("test")
class YorkieControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    YorkieService yorkieService;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void Yorkie_Id를_정상적으로_저장한다()throws Exception{
        //given
        YorkieRegisterRequest yorkieRegisterRequest = new YorkieRegisterRequest(1L,1L);
        YorkieRegisterResponse response = YorkieRegisterResponse.from(YorkieFixture.createYorkie());
        when(yorkieService.register(yorkieRegisterRequest.getYorkieId(),yorkieRegisterRequest.getProjectId())).thenReturn(response);

        //when && then
        mockMvc.perform(post("/api/yorkie/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(yorkieRegisterRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.projectId").value(1L))
                .andExpect(jsonPath("$.message").value("정상적으로 저장되었습니다."));
    }

    @Test
    void 프로젝트_id를_통해_Yorkie_id를_정상적으로_반환한다()throws Exception{
        // given
        Long projectId = 1L;
        YorkieSearchResponse response = YorkieSearchResponse.of(1L);
        when(yorkieService.findYorkieId(projectId)).thenReturn(response);
        // when && then
        mockMvc.perform(get("/api/yorkie/"+projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.yorkieId").value(1L));

    }
}