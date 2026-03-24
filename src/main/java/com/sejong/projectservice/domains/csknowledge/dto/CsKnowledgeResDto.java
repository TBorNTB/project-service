package com.sejong.projectservice.domains.csknowledge.dto;

import com.sejong.projectservice.domains.csknowledge.domain.CsKnowledgeAttachment;
import com.sejong.projectservice.domains.csknowledge.domain.CsKnowledgeEntity;
import com.sejong.projectservice.support.common.file.FileUploader;
import com.sejong.projectservice.support.common.internal.response.UserProfileDto;

import java.time.LocalDateTime;
import java.util.List;

public record CsKnowledgeResDto(
        Long id,
        String title,
        String content,
        String description,
        UserProfileDto writerProfile,
        String category,
        String thumbnailUrl,
        List<AttachmentInfo> attachments,
        LocalDateTime createdAt
) {

    public record AttachmentInfo(String fileKey, String originalFileName) {
        public static AttachmentInfo from(CsKnowledgeAttachment attachment) {
            return new AttachmentInfo(attachment.getFileKey(), attachment.getOriginalFileName());
        }
    }

    public static CsKnowledgeResDto from(
            CsKnowledgeEntity csKnowledgeEntity,
            UserProfileDto writerProfile,
            FileUploader fileUploader
    ) {
        String thumbnailUrl = csKnowledgeEntity.getThumbnailKey() != null
                ? fileUploader.getFileUrl(csKnowledgeEntity.getThumbnailKey())
                : null;

        List<AttachmentInfo> attachments = csKnowledgeEntity.getAttachments().stream()
                .map(AttachmentInfo::from)
                .toList();

        return new CsKnowledgeResDto(
                csKnowledgeEntity.getId(),
                csKnowledgeEntity.getTitle(),
                csKnowledgeEntity.getContent(),
                csKnowledgeEntity.getDescription(),
                writerProfile,
                csKnowledgeEntity.getCategoryEntity().getName(),
                thumbnailUrl,
                attachments,
                csKnowledgeEntity.getCreatedAt()
        );
    }
}
