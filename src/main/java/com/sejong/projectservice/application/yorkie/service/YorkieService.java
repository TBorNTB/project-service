package com.sejong.projectservice.application.yorkie.service;

import com.sejong.projectservice.application.util.JwtUtil;
import com.sejong.projectservice.application.yorkie.dto.request.CheckYorkieRequest;
import com.sejong.projectservice.application.yorkie.dto.response.CheckYorkieResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class YorkieService {

    private final JwtUtil jwtUtil;

    public CheckYorkieResponse checkYorkie(CheckYorkieRequest checkYorkieRequest) {
        if (checkYorkieRequest.getMethod().equals(YorkieMethod.ActivateClient)
                || checkYorkieRequest.getMethod().equals(YorkieMethod.DeactivateClient)) {
            return new CheckYorkieResponse(true, String.format("Pass %s method", checkYorkieRequest.getMethod()));
        }

        String token = checkYorkieRequest.getToken();

        // attributes는 null 일 수도 있는 값이다.
        String yorkieDocId = Optional.ofNullable(checkYorkieRequest.getAttributes())
                .filter(att -> !att.isEmpty())
                .map(att -> att.get(0).key)
                .orElseThrow(() -> new IllegalArgumentException("Document ID not found"));

        if (!jwtUtil.validateToken(token)) {
            return new CheckYorkieResponse(false, "Valid Token");
        }

        String userId = jwtUtil.getUserIdFromToken(token);
        // Todo: yorkieDocumentId, userId(nickname) 으로 Project_User 조회
        // Project_User(project_id, user_id)

        return new CheckYorkieResponse(true, "Valid Token");
    }

    public enum YorkieMethod {
        ActivateClient,
        DeactivateClient,
        AttachDocument,
        DetachDocument,
        WatchDocuments,
        PushPull,
    }

    public enum Verb {
        r,
        rw
    }

    public static class DocumentAttribute {

        private String key;
        private Verb verb;

        public DocumentAttribute(String key, Verb verb) {
            this.key = key;
            this.verb = verb;
        }
    }
}
