package com.sejong.projectservice.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//@FeignClient(name = "user-service")
//public interface UserClient {
//    @GetMapping("/users/{id}")
//    String getUserById(@PathVariable("id") String id);
//
//    @GetMapping("/users/{userId}/exists")
//    boolean exists(@PathVariable("userId") String userId);
//}
