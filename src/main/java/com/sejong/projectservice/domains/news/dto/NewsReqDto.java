package com.sejong.projectservice.domains.news.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewsReqDto {
    String title;
    String summary;
    String content;
    String category;

    @Schema(hidden = true)
    String writerUsername;
    List<String> participantIds;
    List<String> tags;

}
