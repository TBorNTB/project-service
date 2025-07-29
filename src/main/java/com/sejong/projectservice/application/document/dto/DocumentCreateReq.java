package com.sejong.projectservice.application.document.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentCreateReq {
    private String title;
    private String content;
    private String description;
    private String thumbnailUrl;
}
