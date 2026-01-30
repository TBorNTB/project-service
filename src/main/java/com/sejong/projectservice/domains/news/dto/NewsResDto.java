package com.sejong.projectservice.domains.news.dto;

import com.sejong.projectservice.domains.news.domain.NewsEntity;
import com.sejong.projectservice.support.common.file.FileUploader;
import com.sejong.projectservice.support.common.internal.response.UserNameInfo;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record NewsResDto(
        Long id,
        String title,
        String summary,
        String content,
        String category,
        String thumbnailUrl,
        String writerId,
        String writerNickname,
        List<String> participantIds,
        List<String> participantNicknames,
        List<String> tags,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static NewsResDto from(NewsEntity newsEntity, Map<String, UserNameInfo> usernamesMap,
                                  FileUploader fileUploader) {
        UserNameInfo writerInfo = usernamesMap.get(newsEntity.getWriterId());
        String thumbnailUrl = newsEntity.getThumbnailKey() != null
                ? fileUploader.getFileUrl(newsEntity.getThumbnailKey())
                : null;

        return new NewsResDto(
                newsEntity.getId(),
                newsEntity.toContentVo().getTitle(),
                newsEntity.toContentVo().getSummary(),
                newsEntity.toContentVo().getContent(),
                newsEntity.toContentVo().getCategory().name(),
                thumbnailUrl,
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
}
