package com.sejong.projectservice.domains.csknowledge.kafka;


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

    public static CsKnowledgeEvent from(CsKnowledgeDto csKnowledgeDto) {
        return CsKnowledgeEvent.builder()
                .id(csKnowledgeDto.getId().toString())
                .title(csKnowledgeDto.getTitle())
                .writerId(csKnowledgeDto.getWriterId().userId())
                .content(csKnowledgeDto.getContent())
                .category(csKnowledgeDto.getCategory())
                .createdAt(csKnowledgeDto.getCreatedAt().truncatedTo(ChronoUnit.MILLIS).format(FORMATTER))
                .build();
    }
}