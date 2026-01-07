package com.sejong.projectservice.domains.yorkie.service;

import com.sejong.projectservice.domains.collaborator.repository.CollaboratorRepository;
import com.sejong.projectservice.support.common.util.JwtUtil;
import com.sejong.projectservice.domains.yorkie.dto.request.CheckYorkieRequest;
import com.sejong.projectservice.domains.yorkie.dto.response.CheckYorkieResponse;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class YorkieService {

    private final JwtUtil jwtUtil;
    private final CollaboratorRepository collaboratorRepository;

    public ResponseEntity<CheckYorkieResponse> checkYorkie(CheckYorkieRequest checkYorkieRequest) {
        if (checkYorkieRequest.getMethod().equals(YorkieMethod.ActivateClient)
                || checkYorkieRequest.getMethod().equals(YorkieMethod.DeactivateClient)) {
            CheckYorkieResponse response = new CheckYorkieResponse(true,
                    String.format("Pass %s method", checkYorkieRequest.getMethod()));

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        }

        String token = checkYorkieRequest.getToken();

        // attributes는 null 일 수도 있는 값이다.
        String yorkieDocId = Optional.ofNullable(checkYorkieRequest.getAttributes())
                .filter(att -> !att.isEmpty())
                .map(att -> att.get(0).key)
                .orElseThrow(() -> new IllegalArgumentException("Document ID not found"));

        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new CheckYorkieResponse(false, "Token is expired or invalid"));
        }

        String username = jwtUtil.getUserNameFromToken(token);

        boolean belongTo = collaboratorRepository.existsByYorkieDocIdAndUsername(yorkieDocId, username);
        if (!belongTo) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new CheckYorkieResponse(false, "User does not have access to the document"));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new CheckYorkieResponse(true, "Valid Token"));
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
