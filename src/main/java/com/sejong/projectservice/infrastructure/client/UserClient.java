package com.sejong.projectservice.infrastructure.client;


import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service", path = "/internal")
public interface UserClient {
    @GetMapping("/{userId}/exists")
    ResponseEntity<Boolean> exists(@PathVariable("userId") String userId);

    @GetMapping("/exists")
    ResponseEntity<Boolean> existAll(@RequestBody List<String> userNames);
}
