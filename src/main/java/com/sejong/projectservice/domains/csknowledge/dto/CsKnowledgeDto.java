package com.sejong.projectservice.domains.csknowledge.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CsKnowledgeDto {

    private Long id;
    private String title;
    private String content;
    private String writerId;
    private String category;
    private LocalDateTime createdAt;
}
