package com.sejong.projectservice.application.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        servers = {
                @Server(url = "/project-service"),
                @Server(url = "/")
        },
        info = @Info(
                title = "Project API",
                version = "v1",
                description = "프로젝트 서비스 API 문서입니다."
        )
)
@Configuration
public class SwaggerConfig {
}