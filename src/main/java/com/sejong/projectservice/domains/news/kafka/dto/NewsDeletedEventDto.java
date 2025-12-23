package com.sejong.projectservice.domains.news.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewsDeletedEventDto {

    private Long postId;

    public static NewsDeletedEventDto of(Long postId) {
        return new NewsDeletedEventDto(postId);
    }
}
