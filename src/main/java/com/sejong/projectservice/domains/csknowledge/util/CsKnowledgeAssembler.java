package com.sejong.projectservice.domains.csknowledge.util;



import com.sejong.projectservice.domains.csknowledge.dto.CsKnowledgeReqDto;
import com.sejong.projectservice.domains.csknowledge.domain.CsKnowledge;
import com.sejong.projectservice.domains.user.UserId;

import java.time.LocalDateTime;

public class CsKnowledgeAssembler {
    
    public static CsKnowledge toCsKnowledge(CsKnowledgeReqDto reqDto, String username) {
        return CsKnowledge.builder()
                .title(reqDto.title())
                .content(reqDto.content())
                .writerId(UserId.of(username))
                .category(reqDto.category())
                .createdAt(LocalDateTime.now())
                .build();
    }
    
    public static CsKnowledge toCsKnowledgeForUpdate(Long id, CsKnowledgeReqDto reqDto, LocalDateTime createdAt, String username) {
        return CsKnowledge.builder()
                .id(id)
                .title(reqDto.title())
                .writerId(UserId.of(username))
                .content(reqDto.content())
                .category(reqDto.category())
                .createdAt(createdAt)
                .build();
    }
}