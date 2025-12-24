package com.sejong.projectservice.domains.news.kafka;


import com.sejong.projectservice.domains.news.domain.NewsEntity;
import com.sejong.projectservice.domains.news.dto.NewsDto;
import com.sejong.projectservice.support.common.constants.Type;
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

    public static NewsIndexEvent of(NewsEntity newsEntity, Type type, long occurredAt) {
        return NewsIndexEvent.builder()
                .newsEvent(NewsEvent.from(newsEntity))
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
