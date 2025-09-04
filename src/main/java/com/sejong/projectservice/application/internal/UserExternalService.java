package com.sejong.projectservice.application.internal;

import static com.sejong.projectservice.application.common.error.code.ErrorCode.INVALID_USERS_NICKNAME;

import com.sejong.projectservice.application.common.error.code.ErrorCode;
import com.sejong.projectservice.application.common.error.exception.ApiException;
import com.sejong.projectservice.infrastructure.client.UserClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserExternalService {

    private final UserClient userClient;

    @CircuitBreaker(name = "user-circuit-breaker", fallbackMethod = "validateExistenceFallback")
    public void validateExistence(List<String> userNicknames) {
        ResponseEntity<Boolean> response = userClient.existAll(userNicknames);
        if (response.getBody() != Boolean.TRUE) {
            throw new ApiException(INVALID_USERS_NICKNAME, "닉네임 목록을 다시 확인하세요.");
        }
    }

    private void validateExistenceFallback(List<String> userNames, Throwable t) {
        log.info("fallback method is called. userNames: {}", userNames);
        if (t instanceof ApiException) {
            throw (ApiException) t;
        }
        
        throw new ApiException(ErrorCode.EXTERNAL_SERVER_ERROR, "잠시 서비스 이용이 불가합니다.");
    }

    @CircuitBreaker(name = "user-circuit-breaker", fallbackMethod = "validateExistenceFallback")
    public void validateExistence(String username,List<String> collaboratorUsernames) {
        ResponseEntity<Boolean> response = userClient.exists(username,collaboratorUsernames);
        if (response.getBody() != Boolean.TRUE) {
            throw new ApiException(INVALID_USERS_NICKNAME, "닉네임 목록을 다시 확인하세요.");
        }
    }

    private void validateExistenceFallback(String username, List<String> collaboratorUsernames, Throwable t) {
        log.info("fallback method is called. userName: {}, collaboratorUserCount : {}", username,collaboratorUsernames.size());
        if (t instanceof ApiException) {
            throw (ApiException) t;
        }

        throw new ApiException(ErrorCode.EXTERNAL_SERVER_ERROR, "잠시 서비스 이용이 불가합니다.");
    }
}
