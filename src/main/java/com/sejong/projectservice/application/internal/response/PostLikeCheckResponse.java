package com.sejong.projectservice.application.internal.response;

import com.sejong.projectservice.core.project.domain.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostLikeCheckResponse {
    private String ownerUsername;
    private boolean isStored;

    public static PostLikeCheckResponse hasOf(Project project, boolean isStored) {
        return PostLikeCheckResponse.builder()
                .ownerUsername(project.getUsername())
                .isStored(isStored)
                .build();
    }

    public static PostLikeCheckResponse hasNotOf() {
        return PostLikeCheckResponse.builder()
                .ownerUsername(null)
                .isStored(false)
                .build();
    }
}
