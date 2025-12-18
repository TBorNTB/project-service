package com.sejong.projectservice.application.internal.response;

import com.sejong.projectservice.core.csknowledge.CsKnowledge;
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

    public static PostLikeCheckResponse hasOfNews(News news, boolean isStored) {
        return PostLikeCheckResponse.builder()
                .ownerUsername(news.getWriterId().userId())
                .isStored(isStored)
                .build();
    }

    public static PostLikeCheckResponse hasOfCS(CsKnowledge csKnowledge, boolean isStored) {
        return PostLikeCheckResponse.builder()
                .ownerUsername(csKnowledge.getWriterId().userId())
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
