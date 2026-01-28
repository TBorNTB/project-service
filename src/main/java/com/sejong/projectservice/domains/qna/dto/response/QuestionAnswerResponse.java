package com.sejong.projectservice.domains.qna.dto.response;

import com.sejong.projectservice.domains.qna.domain.QuestionAnswerEntity;
import com.sejong.projectservice.support.common.internal.response.UserNameInfo;

import java.time.LocalDateTime;
import java.util.Map;

public record QuestionAnswerResponse(
        Long id,
        Long questionId,
        String content,
        String username,
		String nickname,
		String realName,
        boolean accepted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static QuestionAnswerResponse from(QuestionAnswerEntity entity) {
        return from(entity, Map.of());
    }

    public static QuestionAnswerResponse from(QuestionAnswerEntity entity, Map<String, UserNameInfo> userNameInfos) {
        Long questionId = entity.getQuestionEntity() == null ? null : entity.getQuestionEntity().getId();

        UserNameInfo userNameInfo = userNameInfos.get(entity.getUsername());
        String nickname = userNameInfo == null ? null : userNameInfo.nickname();
        String realName = userNameInfo == null ? null : userNameInfo.realName();

        return new QuestionAnswerResponse(
                entity.getId(),
                questionId,
                entity.getContent(),
                entity.getUsername(),
                nickname,
                realName,
                entity.getAccepted(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
