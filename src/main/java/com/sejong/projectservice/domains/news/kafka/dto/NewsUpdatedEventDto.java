package com.sejong.projectservice.domains.news.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewsUpdatedEventDto {
    private Long postId;

    public static NewsUpdatedEventDto of(Long postId) {
        return new NewsUpdatedEventDto(postId);
    }
}
