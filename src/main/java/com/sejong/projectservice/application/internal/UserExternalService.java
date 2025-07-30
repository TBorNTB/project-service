package com.sejong.projectservice.application.internal;

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
    public void validateExistence(List<String> userNames) {
        ResponseEntity<Boolean> response = userClient.existAll(userNames);
        if (Boolean.FALSE.equals(response.getBody())) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "존재하지 않는 Username");
        }
    }

    private void validateExistenceFallback(List<String> userNames, Throwable t) {
        log.info("fallback method is called. userNames: {}", userNames);
        throw new ApiException(ErrorCode.EXTERNAL_SERVER_ERROR, t.getMessage());
    }
}
