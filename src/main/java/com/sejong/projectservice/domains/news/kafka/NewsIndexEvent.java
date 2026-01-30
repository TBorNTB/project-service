package com.sejong.projectservice.domains.news.kafka;


import com.sejong.projectservice.domains.news.domain.NewsEntity;
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
    private String aggregatedId;
    private NewsEvent newsEvent;
    private Type type;
    private long occurredAt;

    public static NewsIndexEvent of(NewsEntity newsEntity, Type type, long occurredAt) {
        return NewsIndexEvent.builder()
                .aggregatedId(
                        newsEntity.getId().toString())// 추후 outbox패턴 도입시 필요할 수 있어 이대로 유지 elastic 서비스는 동기화 했습니다. 필드명
                .newsEvent(NewsEvent.from(newsEntity))
                .type(type)
                .occurredAt(occurredAt)
                .build();
    }

    public static NewsIndexEvent deleteOf(String newsId, Type type, long occurredAt) {
        return NewsIndexEvent.builder()
                .aggregatedId(newsId)
                .newsEvent(null)
                .type(type)
                .occurredAt(occurredAt)
                .build();
    }
}
