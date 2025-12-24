package com.sejong.projectservice.support.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.sejong.projectservice.client")
public class FeignClientConfig {
}
