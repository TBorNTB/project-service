package com.sejong.projectservice.domains.csknowledge.dto;

import com.sejong.projectservice.domains.csknowledge.domain.CsKnowledgeEntity;
import com.sejong.projectservice.support.common.file.FileUploader;

import java.time.LocalDateTime;

public record CsKnowledgeResDto(
        Long id,
        String title,
        String content,
        String writerId,
        String nickname,
        String category,
        String thumbnailUrl,
        LocalDateTime createdAt
) {

    public static CsKnowledgeResDto from(CsKnowledgeEntity csKnowledgeEntity, String nickname,
                                          FileUploader fileUploader) {
        String thumbnailUrl = csKnowledgeEntity.getThumbnailKey() != null
                ? fileUploader.getFileUrl(csKnowledgeEntity.getThumbnailKey())
                : null;

        return new CsKnowledgeResDto(
                csKnowledgeEntity.getId(),
                csKnowledgeEntity.getTitle(),
                csKnowledgeEntity.getContent(),
                csKnowledgeEntity.getWriterId(),
                nickname,
                csKnowledgeEntity.getCategoryEntity().getName(),
                thumbnailUrl,
                csKnowledgeEntity.getCreatedAt()
        );
    }
}