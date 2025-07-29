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
                title = "Newsletter API",
                version = "v1",
                description = "뉴스레터 API 문서입니다."
        )
)
@Configuration
public class SwaggerConfig {
}