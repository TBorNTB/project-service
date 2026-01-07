package com.sejong.projectservice.domains.news.dto;

import com.sejong.projectservice.domains.news.domain.NewsEntity;
import com.sejong.projectservice.support.common.internal.response.UserNameInfo;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
public record NewsResDto(
        Long id,
        String title,
        String summary,
        String content,
        String category,
        String thumbnailPath,
        String writerId,
        String writerNickname,
        List<String> participantIds,
        List<String> participantNicknames,
        List<String> tags,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static NewsResDto from(NewsDto archive) {
        return new NewsResDto(
                archive.getId(),
                archive.getContent().getTitle(),
                archive.getContent().getSummary(),
                archive.getContent().getContent(),
                archive.getContent().getCategory().name(),
                archive.getThumbnailPath() != null ? archive.getThumbnailPath().path() : null,
                archive.getWriterId().userId(),
                null, // writerNickname 없음
                archive.getParticipantIds().toList(),
                List.of(), // participantNicknames 없음
                archive.getTags(),
                archive.getCreatedAt(),
                archive.getUpdatedAt()
        );
    }

    public static NewsResDto from(NewsEntity newsEntity, Map<String, UserNameInfo> usernamesMap) {
        UserNameInfo writerInfo = usernamesMap.get(newsEntity.getWriterId());
        return new NewsResDto(
                newsEntity.getId(),
                newsEntity.toContentVo().getTitle(),
                newsEntity.toContentVo().getSummary(),
                newsEntity.toContentVo().getContent(),
                newsEntity.toContentVo().getCategory().name(),
                newsEntity.toFilepathVo().path() != null ? newsEntity.toFilepathVo().path() : null,
                newsEntity.getWriterId(),
                writerInfo != null ? writerInfo.nickname() : null,
                newsEntity.toParticipantIdsVo().toList(),
                newsEntity.toParticipantIdsVo().toList().stream()
                        .map(userId -> {
                            UserNameInfo userInfo = usernamesMap.get(userId);
                            return userInfo != null ? userInfo.nickname() : null;
                        })
                        .toList(),
                newsEntity.toTagsList(),
                newsEntity.getCreatedAt(),
                newsEntity.getUpdatedAt()
        );
    }

    private static List<String> extractUserIds(String participantIds) {
        if (participantIds == null || participantIds.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(participantIds.split(","))
                .toList();
    }
}
