package com.sejong.projectservice.domains.news.dto;

import com.sejong.projectservice.domains.news.domain.NewsEntity;
import com.sejong.projectservice.support.common.file.FileUploader;
import com.sejong.projectservice.support.common.internal.response.UserNameInfo;
import com.sejong.projectservice.support.common.internal.response.UserProfileDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.Builder;

@Builder
public record NewsResDto(
        Long id,
        String title,
        String summary,
        String content,
        String category,
        String thumbnailUrl,
        UserProfileDto writerProfile,
        List<UserProfileDto> participantProfiles,
        List<String> tags,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static NewsResDto from(
            NewsEntity newsEntity,
            Map<String, UserNameInfo> usernamesMap,
            FileUploader fileUploader
    ) {
        UserProfileDto writerProfile = UserProfileDto.from(newsEntity.getWriterUsername(),
                usernamesMap.get(newsEntity.getWriterUsername()));

        String thumbnailUrl = (newsEntity.getThumbnailKey() != null && !newsEntity.getThumbnailKey().isEmpty())
                ? fileUploader.getFileUrl(newsEntity.getThumbnailKey())
                : null;

        List<UserProfileDto> participantProfiles = newsEntity.toParticipantUsernameList().stream()
                .map(userId -> UserProfileDto.from(userId, usernamesMap.get(userId)))
                .toList();

        return NewsResDto.builder()
                .id(newsEntity.getId())
                .title(newsEntity.toContentVo().getTitle())
                .summary(newsEntity.toContentVo().getSummary())
                .content(newsEntity.toContentVo().getContent())
                .category(newsEntity.toContentVo().getCategory().name())
                .thumbnailUrl(thumbnailUrl)
                .writerProfile(writerProfile)
                .participantProfiles(participantProfiles)
                .tags(newsEntity.toTagsList())
                .createdAt(newsEntity.getCreatedAt())
                .updatedAt(newsEntity.getUpdatedAt())
                .build();
    }
}
