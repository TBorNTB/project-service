package com.sejong.projectservice.domains.document.kafka.dto;

import com.sejong.projectservice.domains.csknowledge.kafka.dto.CsKnowledgeCreatedEventDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentCreatedEventDto {
    private Long postId;

    public static DocumentCreatedEventDto of(Long postId){
        return new DocumentCreatedEventDto(postId);
    }
}
