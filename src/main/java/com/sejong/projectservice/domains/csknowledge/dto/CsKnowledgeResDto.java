package com.sejong.projectservice.domains.csknowledge.dto;

import com.sejong.projectservice.domains.csknowledge.domain.CsKnowledgeEntity;
import com.sejong.projectservice.support.common.file.FileUploader;
import com.sejong.projectservice.support.common.internal.response.UserProfileDto;

import java.time.LocalDateTime;

public record CsKnowledgeResDto(
        Long id,
        String title,
        String content,
        UserProfileDto writerProfile,
        String category,
        String thumbnailUrl,
        LocalDateTime createdAt
) {

    public static CsKnowledgeResDto from(
            CsKnowledgeEntity csKnowledgeEntity,
            UserProfileDto writerProfile,
            FileUploader fileUploader
    ) {
        String thumbnailUrl = csKnowledgeEntity.getThumbnailKey() != null
                ? fileUploader.getFileUrl(csKnowledgeEntity.getThumbnailKey())
                : null;

        return new CsKnowledgeResDto(
                csKnowledgeEntity.getId(),
                csKnowledgeEntity.getTitle(),
                csKnowledgeEntity.getContent(),
                writerProfile,
                csKnowledgeEntity.getCategoryEntity().getName(),
                thumbnailUrl,
                csKnowledgeEntity.getCreatedAt()
        );
    }
}
