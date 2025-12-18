package com.sejong.projectservice.client;

import java.util.List;
import java.util.Map;

import com.sejong.projectservice.client.response.UserNameInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", path = "/internal")
public interface UserClient {
    @GetMapping("/{username}/exists")
    ResponseEntity<Boolean> exists(@PathVariable("username") String username);

    @GetMapping("/exists")
    ResponseEntity<Boolean> existAll(@RequestBody List<String> nicknames);

    @GetMapping("/{username}/exists/multiple")
    ResponseEntity<Boolean> exists(@PathVariable("username") String username,
                                   @RequestParam("collaboratorUsernames") List<String> collaboratorUsernames);

    @GetMapping("/un-info")
    ResponseEntity<Map<String, UserNameInfo>> getUserNameInfos(@RequestParam("usernames") List<String> usernames);


}
