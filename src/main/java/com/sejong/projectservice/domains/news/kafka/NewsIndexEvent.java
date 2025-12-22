package com.sejong.projectservice.domains.news.kafka;


import com.sejong.projectservice.domains.news.domain.NewsDto;
import com.sejong.projectservice.domains.project.kafka.enums.Type;
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

    public static NewsIndexEvent of(NewsDto newsDto, Type type, long occurredAt) {
        return NewsIndexEvent.builder()
                .newsEvent(NewsEvent.from(newsDto))
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
