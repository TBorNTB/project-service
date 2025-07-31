package com.sejong.projectservice.application.common.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.sejong.projectservice.infrastructure.client")
public class FeignClientConfig {
}
