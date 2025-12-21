package com.sejong.projectservice.application.project.config;

import com.sejong.projectservice.domains.project.service.ProjectService;
import com.sejong.projectservice.domains.yorkie.service.YorkieService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class MockBeansConfig {

    @Bean
    public ProjectService projectService() {
        return mock(ProjectService.class);
    }

    @Bean
    public YorkieService yorkieService() {
        return mock(YorkieService.class);
    }
}
