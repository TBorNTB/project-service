package com.sejong.projectservice.domains.news.domain;

import com.sejong.projectservice.support.common.exception.BaseException;
import com.sejong.projectservice.support.common.exception.ExceptionType;
import com.sejong.projectservice.support.common.file.Filepath;
import com.sejong.projectservice.domains.user.UserId;
import com.sejong.projectservice.domains.user.UserIds;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@EqualsAndHashCode
@ToString
@Builder
@AllArgsConstructor
public class NewsDto {

    private Long id;
    private Content content;
    private Filepath thumbnailPath;
    private UserId writerId;
    private UserIds participantIds;
    private List<String> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static NewsDto create(Content content, UserId writerId, UserIds participantIds, List<String> tags) {
        return NewsDto.builder()
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
}