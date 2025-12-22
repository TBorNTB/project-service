package com.sejong.projectservice.support.common.internal.response;

import com.sejong.projectservice.domains.csknowledge.dto.CsKnowledgeDto;
import com.sejong.projectservice.domains.news.dto.NewsDto;
import com.sejong.projectservice.domains.project.domain.ProjectEntity;
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

    public static PostLikeCheckResponse hasOfNews(NewsDto newsDto, boolean isStored) {
        return PostLikeCheckResponse.builder()
                .ownerUsername(newsDto.getWriterId().userId())
                .isStored(isStored)
                .build();
    }

    public static PostLikeCheckResponse hasOfCS(CsKnowledgeDto csKnowledgeDto, boolean isStored) {
        return PostLikeCheckResponse.builder()
                .ownerUsername(csKnowledgeDto.getWriterId().userId())
                .isStored(isStored)
                .build();
    }
    public static PostLikeCheckResponse hasNotOf() {
        return PostLikeCheckResponse.builder()
                .ownerUsername(null)
                .isStored(false)
                .build();
    }

    public static PostLikeCheckResponse hasOfProject(ProjectEntity project, boolean isStored) {
        return PostLikeCheckResponse.builder()
                .ownerUsername(project.getUsername())
                .isStored(false)
                .build();
    }
}
