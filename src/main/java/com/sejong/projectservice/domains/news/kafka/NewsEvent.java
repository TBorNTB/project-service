package com.sejong.projectservice.domains.news.kafka;

import com.sejong.projectservice.domains.news.domain.Content;
import com.sejong.projectservice.domains.news.domain.NewsDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewsEvent {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    private String id;
    private Content content;
    private String thumbnailPath;
    private String writerId;
    private List<String> participantIds;
    private List<String> tags;
    private String createdAt;
    private String updatedAt;

    public static NewsEvent from(NewsDto newsDto) {
        return NewsEvent.builder()
                .id(newsDto.getId().toString())
                .content(newsDto.getContent())
                .thumbnailPath(newsDto.getThumbnailPath() != null ? newsDto.getThumbnailPath().path() : null)
                .writerId(newsDto.getWriterId().userId())
                .participantIds(newsDto.getParticipantIds() != null ? newsDto.getParticipantIds().toList() : null)
                .tags(newsDto.getTags())
                .createdAt(newsDto.getCreatedAt().truncatedTo(ChronoUnit.MILLIS).format(FORMATTER))
                .updatedAt(newsDto.getUpdatedAt().truncatedTo(ChronoUnit.MILLIS).format(FORMATTER))
                .build();
    }
}
