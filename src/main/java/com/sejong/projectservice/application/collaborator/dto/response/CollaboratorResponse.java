package com.sejong.projectservice.application.collaborator.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CollaboratorResponse {
    private Long id;
    private String username;
    private String nickname;

    public static CollaboratorResponse of(Long id, String username, String nickname) {
        return CollaboratorResponse.builder()
                .id(id)
                .username(username)
                .nickname(nickname)
                .build();
    }
}
