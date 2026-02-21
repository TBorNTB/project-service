package com.sejong.projectservice.domains.news.dto.event;


import com.sejong.projectservice.domains.news.domain.NewsEntity;
import com.sejong.projectservice.support.common.constants.Type;
import com.sejong.projectservice.support.common.file.FileUploader;
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

    public static NewsIndexEvent of(NewsEntity newsEntity, FileUploader fileUploader, Type type, long occurredAt) {
        return NewsIndexEvent.builder()
                .aggregatedId(newsEntity.getId().toString())
                .newsEvent(NewsEvent.from(newsEntity, fileUploader))
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
