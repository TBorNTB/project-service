package com.sejong.projectservice.domains.csknowledge.kafka;


import com.sejong.projectservice.domains.csknowledge.domain.CsKnowledgeEntity;
import com.sejong.projectservice.domains.csknowledge.dto.CsKnowledgeDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CsKnowledgeEvent {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    private String id;
    private String title;
    private String content;
    private String writerId;
    private String category;
    private String createdAt;

    public static CsKnowledgeEvent from(CsKnowledgeEntity csKnowledgeEntity) {
        return CsKnowledgeEvent.builder()
                .id(csKnowledgeEntity.getId().toString())
                .title(csKnowledgeEntity.getTitle())
                .writerId(csKnowledgeEntity.getWriterId())
                .content(csKnowledgeEntity.getContent())
                .category(csKnowledgeEntity.getCategoryEntity().getName())
                .createdAt(csKnowledgeEntity.getCreatedAt().truncatedTo(ChronoUnit.MILLIS).format(FORMATTER))
                .build();
    }
}