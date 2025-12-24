package com.sejong.projectservice.domains.collaborator.dto;

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
    private String realname;

    public static CollaboratorResponse of(Long id, String username, String nickname, String realName) {
        return CollaboratorResponse.builder()
                .id(id)
                .username(username)
                .nickname(nickname)
                .realname(realName)
                .build();
    }
}
