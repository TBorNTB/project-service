package com.sejong.projectservice.support.common.internal.response;

import com.sejong.projectservice.domains.csknowledge.domain.CsKnowledgeEntity;
import com.sejong.projectservice.domains.news.domain.NewsEntity;
import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import com.sejong.projectservice.domains.qna.domain.QuestionAnswerEntity;
import com.sejong.projectservice.domains.qna.domain.QuestionEntity;
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

    public static PostLikeCheckResponse hasOfNews(NewsEntity newsEntity, boolean isStored) {
        return PostLikeCheckResponse.builder()
                .ownerUsername(newsEntity.getWriterId())
                .isStored(isStored)
                .build();
    }

    public static PostLikeCheckResponse hasOfCS(CsKnowledgeEntity csKnowledgeEntity, boolean isStored) {
        return PostLikeCheckResponse.builder()
                .ownerUsername(csKnowledgeEntity.getWriterId())
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
                .isStored(isStored)
                .build();
    }

    public static PostLikeCheckResponse hasOfQuestion(QuestionEntity question, boolean isStored) {
        return PostLikeCheckResponse.builder()
                .ownerUsername(question.getUsername())
                .isStored(isStored)
                .build();
    }

    public static PostLikeCheckResponse hasOfAnswer(QuestionAnswerEntity answer, boolean isStored) {
        return PostLikeCheckResponse.builder()
                .ownerUsername(answer.getUsername())
                .isStored(isStored)
                .build();
    }
}
