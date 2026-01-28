package com.sejong.projectservice.client;

import com.sejong.projectservice.support.common.internal.response.PostLikeCheckResponse;
import com.sejong.projectservice.support.common.internal.response.UserNameInfo;
import java.util.List;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", path = "/internal")
public interface UserClient {
    @GetMapping("/{username}/exists")
    ResponseEntity<Boolean> exists(@PathVariable("username") String username);

    @GetMapping("/{username}/exists/multiple")
    ResponseEntity<Boolean> exists(@PathVariable("username") String username,
                                   @RequestParam("collaboratorUsernames") List<String> collaboratorUsernames);

    @GetMapping("/un-info")
    ResponseEntity<Map<String, UserNameInfo>> getUserNameInfos(@RequestParam("usernames") List<String> usernames);

    @GetMapping("/qna/check/question/{questionId}")
    ResponseEntity<PostLikeCheckResponse> checkQnaQuestion(@PathVariable("questionId") Long questionId);

    @GetMapping("/qna/check/answer/{answerId}")
    ResponseEntity<PostLikeCheckResponse> checkQnaAnswer(@PathVariable("answerId") Long answerId);


}
