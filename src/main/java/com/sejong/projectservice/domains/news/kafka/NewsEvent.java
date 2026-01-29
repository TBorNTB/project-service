package com.sejong.projectservice.domains.news.kafka;

import com.sejong.projectservice.domains.news.domain.Content;
import com.sejong.projectservice.domains.news.domain.NewsEntity;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewsEvent {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    private String id;
    private Content content;
    private String thumbnailKey;
    private String writerId;
    private List<String> participantIds;
    private List<String> tags;
    private String createdAt;
    private String updatedAt;

    public static NewsEvent from(NewsEntity newsEntity) {
        return NewsEvent.builder()
                .id(newsEntity.getId().toString())
                .content(newsEntity.toContentVo())
                .thumbnailKey(newsEntity.getThumbnailKey())
                .writerId(newsEntity.getWriterId())
                .participantIds(
                        newsEntity.toParticipantIdsVo() != null ? newsEntity.toParticipantIdsVo().toList() : null)
                .tags(newsEntity.toTagsList())
                .createdAt(newsEntity.getCreatedAt().truncatedTo(ChronoUnit.MILLIS).format(FORMATTER))
                .updatedAt(newsEntity.getUpdatedAt().truncatedTo(ChronoUnit.MILLIS).format(FORMATTER))
                .build();
    }
}
