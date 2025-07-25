package com.sejong.projectservice.application.project.dto.request;

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
