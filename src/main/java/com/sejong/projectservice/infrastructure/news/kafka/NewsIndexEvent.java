package com.sejong.archiveservice.infrastructure.news.kafka;

import com.sejong.archiveservice.core.news.News;
import com.sejong.archiveservice.infrastructure.common.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewsIndexEvent {
    private NewsEvent newsEvent;
    private Type type;
    private long occurredAt;

    public static NewsIndexEvent of(News news, Type type, long occurredAt) {
        return NewsIndexEvent.builder()
                .newsEvent(NewsEvent.from(news))
                .type(type)
                .occurredAt(occurredAt)
                .build();
    }

    public static NewsIndexEvent deleteOf(String newsId, Type type, long occurredAt) {
        return NewsIndexEvent.builder()
                .newsEvent(null)
                .type(type)
                .occurredAt(occurredAt)
                .build();
    }
}
