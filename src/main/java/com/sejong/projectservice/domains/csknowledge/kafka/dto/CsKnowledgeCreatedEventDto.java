package com.sejong.projectservice.domains.csknowledge.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CsKnowledgeCreatedEventDto {
    private Long postId;

    public static CsKnowledgeCreatedEventDto of(Long postId){
        return new CsKnowledgeCreatedEventDto(postId);
    }
}
