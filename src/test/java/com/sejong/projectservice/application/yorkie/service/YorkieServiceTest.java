package com.sejong.projectservice.application.yorkie.service;

import com.sejong.projectservice.application.yorkie.controller.fixture.YorkieFixture;
import com.sejong.projectservice.application.yorkie.dto.response.YorkieRegisterResponse;
import com.sejong.projectservice.application.yorkie.dto.response.YorkieSearchResponse;
import com.sejong.projectservice.core.yorkie.Yorkie;
import com.sejong.projectservice.core.yorkie.YorkieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class YorkieServiceTest {

    YorkieService yorkieService;
    @Mock
    YorkieRepository yorkieRepository;

    @BeforeEach
    void setUp() {
        yorkieService = new YorkieService(yorkieRepository);
    }

    @Test
    void Yorkie_Id를_저장한다() {
        // given
        Long yorkieId = 1L;
        Long projectId = 1L;
        Yorkie savedYorkie = YorkieFixture.createYorkie();
        when(yorkieRepository.save(any(Yorkie.class))).thenReturn(savedYorkie);

        // when
        YorkieRegisterResponse response = yorkieService.register(yorkieId, projectId);

        // then
        assertThat(response.getYorkieId()).isEqualTo(yorkieId);
        assertThat(response.getProjectId()).isEqualTo(projectId);
    }

    @Test
    void 프로젝트_id를_통해_Yorkie_id를_조회한다() {
        // given
        Long projectId = 1L;
        Long yorkieId = 1L;
        when(yorkieRepository.findByProjectId(projectId)).thenReturn(yorkieId);

        // when
        YorkieSearchResponse response = yorkieService.findYorkieId(projectId);

        // then
        assertThat(response.getYorkieId()).isEqualTo(yorkieId);
    }
}