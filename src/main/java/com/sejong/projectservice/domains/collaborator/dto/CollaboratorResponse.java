package com.sejong.projectservice.domains.collaborator.dto;

import com.sejong.projectservice.support.common.internal.response.UserProfileDto;
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
    private UserProfileDto profile;

    public static CollaboratorResponse of(Long id, UserProfileDto profile) {
        return CollaboratorResponse.builder()
                .id(id)
                .profile(profile)
                .build();
    }
}
