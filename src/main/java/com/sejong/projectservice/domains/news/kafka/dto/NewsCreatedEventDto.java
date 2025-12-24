package com.sejong.projectservice.domains.news.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewsCreatedEventDto {
    private Long postId;

    public static NewsCreatedEventDto of(Long postId) {
        return new NewsCreatedEventDto(postId);
    }
}
