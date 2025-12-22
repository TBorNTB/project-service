package com.sejong.projectservice.domains.news.util;


import com.sejong.projectservice.support.common.file.Filepath;
import com.sejong.projectservice.domains.news.domain.Content;
import com.sejong.projectservice.domains.news.domain.NewsDto;
import com.sejong.projectservice.domains.user.UserId;
import com.sejong.projectservice.domains.user.UserIds;
import com.sejong.projectservice.domains.news.domain.ContentEmbeddable;
import com.sejong.projectservice.domains.news.domain.NewsEntity;

import java.util.Arrays;
import java.util.List;

public class NewsMapper {
    public static NewsDto toDomain(NewsEntity newsEntity) {
        ContentEmbeddable contentEbd = newsEntity.getContent();
        Content content = Content.of(contentEbd.getTitle(),
                contentEbd.getSummary(),
                contentEbd.getContent(),
                contentEbd.getCategory());
        List<String> tags = Arrays.stream(newsEntity.getTags().split(",")).toList();

        return NewsDto.builder()
                .id(newsEntity.getId())
                .content(content)
                .thumbnailPath(Filepath.of(newsEntity.getThumbnailPath()))
                .writerId(UserId.of(newsEntity.getWriterId()))
                .participantIds(UserIds.of(newsEntity.getParticipantIds()))
                .tags(tags)
                .createdAt(newsEntity.getCreatedAt())
                .updatedAt(newsEntity.getUpdatedAt())
                .build();
    }

    public static NewsEntity toEntity(NewsDto newsDto) {
        return NewsEntity.builder()
                .id(newsDto.getId())
                .content(ContentEmbeddable.of(newsDto.getContent()))
                .thumbnailPath(newsDto.getThumbnailPath() == null ? null : newsDto.getThumbnailPath().path())
                .writerId(newsDto.getWriterId().userId())
                .participantIds(newsDto.getParticipantIds().toString())
                .tags(newsDto.getTags().toString())
                .createdAt(newsDto.getCreatedAt())
                .updatedAt(newsDto.getUpdatedAt())
                .build();
    }
}
