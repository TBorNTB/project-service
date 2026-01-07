package com.sejong.projectservice.domains.csknowledge.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CsKnowledgeDeletedEventDto {
    private Long postId;

    public static CsKnowledgeDeletedEventDto of(Long postId){
        return new CsKnowledgeDeletedEventDto(postId);
    }
}
