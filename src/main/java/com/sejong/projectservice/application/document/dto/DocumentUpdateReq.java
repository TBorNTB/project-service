package com.sejong.projectservice.application.document.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentUpdateReq {
    private String title;
    private String description;
    private String content;
    private String thumbnailUrl;
}
