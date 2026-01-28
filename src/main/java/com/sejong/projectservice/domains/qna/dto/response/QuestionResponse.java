package com.sejong.projectservice.domains.qna.dto.response;

import com.sejong.projectservice.domains.qna.domain.QuestionEntity;
import com.sejong.projectservice.support.common.internal.response.UserNameInfo;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record QuestionResponse(
	Long id,
	String title,
	String description,
	String content,
	String username,
	String nickname,
	String realName,
	List<String> categories,
	String status,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {
    public static QuestionResponse from(QuestionEntity questionEntity) {
	return from(questionEntity, Map.of());
	}

	public static QuestionResponse from(QuestionEntity questionEntity, Map<String, UserNameInfo> userNameInfos) {
	List<String> categories = questionEntity.getQuestionCategories().stream()
		.map(link -> link.getCategoryEntity().getName())
		.distinct()
		.toList();

	UserNameInfo userNameInfo = userNameInfos.get(questionEntity.getUsername());
	String nickname = userNameInfo == null ? null : userNameInfo.nickname();
	String realName = userNameInfo == null ? null : userNameInfo.realName();

	return new QuestionResponse(
		questionEntity.getId(),
		questionEntity.getTitle(),
		questionEntity.getDescription(),
		questionEntity.getContent(),
		questionEntity.getUsername(),
		nickname,
		realName,
		categories,
		questionEntity.getQuestionStatus() == null ? null : questionEntity.getQuestionStatus().name(),
		questionEntity.getCreatedAt(),
		questionEntity.getUpdatedAt()
	);
    }
}
