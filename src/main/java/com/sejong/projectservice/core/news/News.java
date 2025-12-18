package com.sejong.archiveservice.core.news;

import com.sejong.archiveservice.application.exception.BaseException;
import com.sejong.archiveservice.application.exception.ExceptionType;
import com.sejong.archiveservice.core.common.file.Filepath;
import com.sejong.archiveservice.core.user.UserId;
import com.sejong.archiveservice.core.user.UserIds;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
@Builder
@AllArgsConstructor
public class News {

    private Long id;
    private Content content;
    private Filepath thumbnailPath;
    private UserId writerId;
    private UserIds participantIds;
    private List<String> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static News create(Content content, UserId writerId, UserIds participantIds, List<String> tags) {
        return News.builder()
                .id(null)
                .content(content)
                .thumbnailPath(null)
                .writerId(writerId)
                .participantIds(participantIds)
                .tags(tags)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public void update(Content content, UserIds participantIds, List<String> tags) {
        this.content = content;
        this.participantIds = participantIds;
        this.tags = tags;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateFileInfo(Filepath filepath) {
        this.thumbnailPath = filepath;
    }

    public void validateOwner(UserId userId) {
        if (!this.writerId.equals(userId)) {
            throw new BaseException(ExceptionType.NOT_NEWS_OWNER);
        }
    }
}