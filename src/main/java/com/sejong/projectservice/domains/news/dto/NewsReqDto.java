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

    @Schema(description = "썸네일 이미지 key (presigned URL 업로드 후 받은 key)")
    String thumbnailKey;

    @Schema(description = "에디터 본문에 삽입된 이미지 key 목록")
    List<String> contentImageKeys;
}
