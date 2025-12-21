package com.sejong.projectservice.domains.news.util;


import com.sejong.projectservice.support.common.file.Filepath;
import com.sejong.projectservice.domains.news.domain.Content;
import com.sejong.projectservice.domains.news.domain.News;
import com.sejong.projectservice.domains.user.UserId;
import com.sejong.projectservice.domains.user.UserIds;
import com.sejong.projectservice.domains.news.domain.ContentEmbeddable;
import com.sejong.projectservice.domains.news.domain.NewsEntity;

import java.util.Arrays;
import java.util.List;

public class NewsMapper {
    public static News toDomain(NewsEntity newsEntity) {
        ContentEmbeddable contentEbd = newsEntity.getContent();
        Content content = Content.of(contentEbd.getTitle(),
                contentEbd.getSummary(),
                contentEbd.getContent(),
                contentEbd.getCategory());
        List<String> tags = Arrays.stream(newsEntity.getTags().split(",")).toList();

        return News.builder()
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

    public static NewsEntity toEntity(News news) {
        return NewsEntity.builder()
                .id(news.getId())
                .content(ContentEmbeddable.of(news.getContent()))
                .thumbnailPath(news.getThumbnailPath() == null ? null : news.getThumbnailPath().path())
                .writerId(news.getWriterId().userId())
                .participantIds(news.getParticipantIds().toString())
                .tags(news.getTags().toString())
                .createdAt(news.getCreatedAt())
                .updatedAt(news.getUpdatedAt())
                .build();
    }
}
