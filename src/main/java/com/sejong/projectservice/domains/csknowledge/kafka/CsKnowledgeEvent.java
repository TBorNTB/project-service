package com.sejong.projectservice.domains.csknowledge.kafka;


import com.sejong.projectservice.domains.csknowledge.domain.CsKnowledge;
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

    public static CsKnowledgeEvent from(CsKnowledge csKnowledge) {
        return CsKnowledgeEvent.builder()
                .id(csKnowledge.getId().toString())
                .title(csKnowledge.getTitle())
                .writerId(csKnowledge.getWriterId().userId())
                .content(csKnowledge.getContent())
                .category(csKnowledge.getCategory().name())
                .createdAt(csKnowledge.getCreatedAt().truncatedTo(ChronoUnit.MILLIS).format(FORMATTER))
                .build();
    }
}