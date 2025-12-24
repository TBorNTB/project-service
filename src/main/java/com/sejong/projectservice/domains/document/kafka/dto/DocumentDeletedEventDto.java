package com.sejong.projectservice.domains.document.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentDeletedEventDto {
    private Long postId;

    public static DocumentDeletedEventDto of(Long postId){
        return new DocumentDeletedEventDto(postId);
    }
}
