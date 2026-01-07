package com.sejong.projectservice.domains.document.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentUpdatedEventDto {
    private Long postId;

    public static DocumentUpdatedEventDto of(Long postId){
        return new DocumentUpdatedEventDto(postId);
    }
}
