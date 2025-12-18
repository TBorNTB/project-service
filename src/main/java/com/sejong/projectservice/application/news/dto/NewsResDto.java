package com.sejong.archiveservice.application.news.dto;

import com.sejong.archiveservice.client.dto.UserNameInfo;
import com.sejong.archiveservice.core.news.News;
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
    public static NewsResDto from(News archive) {
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

    public static NewsResDto from(News archive, Map<String, UserNameInfo> usernamesMap) {
        return new NewsResDto(
                archive.getId(),
                archive.getContent().getTitle(),
                archive.getContent().getSummary(),
                archive.getContent().getContent(),
                archive.getContent().getCategory().name(),
                archive.getThumbnailPath() != null ? archive.getThumbnailPath().path() : null,
                archive.getWriterId().userId(),
                usernamesMap.get(archive.getWriterId().userId()).nickname(),
                archive.getParticipantIds().toList(),
                archive.getParticipantIds().toList().stream()
                        .map(userId -> usernamesMap.get(userId).nickname())
                        .toList(),
                archive.getTags(),
                archive.getCreatedAt(),
                archive.getUpdatedAt()
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
