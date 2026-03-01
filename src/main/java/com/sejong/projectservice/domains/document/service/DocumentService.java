package com.sejong.projectservice.domains.document.service;

import com.sejong.projectservice.domains.document.domain.DocumentEntity;
import com.sejong.projectservice.domains.document.dto.DocumentCreateReq;
import com.sejong.projectservice.domains.document.dto.DocumentInfoRes;
import com.sejong.projectservice.domains.document.dto.DocumentUpdateReq;
import com.sejong.projectservice.domains.document.repository.DocumentRepository;
import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import com.sejong.projectservice.domains.project.repository.ProjectRepository;
import com.sejong.projectservice.support.common.constants.Type;
import com.sejong.projectservice.support.common.exception.BaseException;
import com.sejong.projectservice.support.common.exception.ExceptionType;
import com.sejong.projectservice.support.common.file.FileUploader;
import com.sejong.projectservice.support.common.internal.UserExternalService;
import com.sejong.projectservice.support.common.sanitizer.RequestSanitizer;
import com.sejong.projectservice.support.outbox.OutBoxFactory;
import com.sejong.projectservice.support.outbox.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final UserExternalService userExternalService;
    private final DocumentRepository documentRepository;
    private final ProjectRepository projectRepository;
    private final OutboxService outboxService;
    private final FileUploader fileUploader;
    private final RequestSanitizer requestSanitizer;

    @Transactional
    public DocumentInfoRes createDocument(Long projectId, DocumentCreateReq request, String username) {
        requestSanitizer.sanitize(request);
        userExternalService.validateExistence(username);
        ProjectEntity projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException(ExceptionType.PROJECT_NOT_FOUND));
        projectEntity.validateUserPermission(username);
        DocumentEntity documentEntity = DocumentEntity.of(
                request.getTitle(),
                request.getDescription(),
                request.getContent(),
                projectEntity
        );

        DocumentEntity savedDocumentEntity = documentRepository.save(documentEntity);

        // 썸네일 파일 처리 (temp → 최종 위치)
        if (request.getThumbnailKey() != null && !request.getThumbnailKey().isEmpty()) {
            String targetDir = String.format("project-service/document/%d/thumbnail", savedDocumentEntity.getId());
            String finalKey = fileUploader.moveFile(request.getThumbnailKey(), targetDir);
            savedDocumentEntity.updateThumbnailKey(finalKey);
        }

        // 에디터 본문 이미지 처리 (temp → 최종 위치) 및 content URL 치환
        if (request.getContentImageKeys() != null && !request.getContentImageKeys().isEmpty()) {
            String updatedContent = processContentImages(
                    savedDocumentEntity.getId(),
                    request.getContent(),
                    request.getContentImageKeys()
            );
            savedDocumentEntity.updateContent(updatedContent);
        }

        OutBoxFactory outbox = OutBoxFactory.of(savedDocumentEntity, fileUploader, Type.CREATED);
        outboxService.enqueue(outbox);
        return DocumentInfoRes.from(savedDocumentEntity, fileUploader);
    }

    @Transactional(readOnly = true)
    public DocumentInfoRes getDocument(Long documentId) {
        DocumentEntity documentEntity = documentRepository.findById(documentId)
                .orElseThrow(() -> new BaseException(ExceptionType.DOCUMENT_NOT_FOUND));
        return DocumentInfoRes.from(documentEntity, fileUploader);
    }

    @Transactional
    public DocumentInfoRes updateDocument(Long documentId, DocumentUpdateReq request, String username) {
        requestSanitizer.sanitize(request);
        DocumentEntity documentEntity = documentRepository.findById(documentId)
                .orElseThrow(() -> new BaseException(ExceptionType.DOCUMENT_NOT_FOUND));
        ProjectEntity projectEntity = projectRepository.findById(documentEntity.getProjectEntity().getId())
                .orElseThrow(() -> new BaseException(ExceptionType.PROJECT_NOT_FOUND));
        projectEntity.validateUserPermission(username);
        documentEntity.update(request.getTitle(), request.getContent(), request.getDescription());

        // 새 썸네일이 전달된 경우 (temp key)
        if (request.getThumbnailKey() != null && !request.getThumbnailKey().isEmpty()) {
            if (documentEntity.getThumbnailKey() != null) {
                try {
                    fileUploader.delete(documentEntity.getThumbnailKey());
                } catch (Exception e) {
                    log.warn("기존 썸네일 삭제 실패, 계속 진행: {}", documentEntity.getThumbnailKey(), e);
                }
            }
            String targetDir = String.format("project-service/document/%d/thumbnail", documentEntity.getId());
            String finalKey = fileUploader.moveFile(request.getThumbnailKey(), targetDir);
            documentEntity.updateThumbnailKey(finalKey);
        }

        // 새 에디터 본문 이미지가 전달된 경우
        if (request.getContentImageKeys() != null && !request.getContentImageKeys().isEmpty()) {
            String updatedContent = processContentImages(
                    documentEntity.getId(),
                    documentEntity.getContent(),
                    request.getContentImageKeys()
            );
            documentEntity.updateContent(updatedContent);
        }

        OutBoxFactory outbox = OutBoxFactory.of(documentEntity, fileUploader, Type.UPDATED);
        outboxService.enqueue(outbox);
        return DocumentInfoRes.from(documentEntity, fileUploader);
    }

    @Transactional
    public void deleteDocument(Long documentId, String username) {
        DocumentEntity documentEntity = documentRepository.findById(documentId)
                .orElseThrow(() -> new BaseException(ExceptionType.DOCUMENT_NOT_FOUND));
        ProjectEntity projectEntity = projectRepository.findById(documentEntity.getProjectEntity().getId())
                .orElseThrow(() -> new BaseException(ExceptionType.PROJECT_NOT_FOUND));
        projectEntity.validateUserPermission(username);
        documentRepository.deleteById(documentEntity.getId());
        OutBoxFactory outbox = OutBoxFactory.remove(documentEntity, Type.DELETED);
        outboxService.enqueue(outbox);
    }

    private String processContentImages(Long documentId, String content, List<String> imageKeys) {
        String updatedContent = content;
        String targetDir = String.format("project-service/document/%d/images", documentId);

        for (String tempKey : imageKeys) {
            if (tempKey == null || tempKey.isEmpty()) {
                continue;
            }
            try {
                String tempUrl = fileUploader.getFileUrl(tempKey);
                String finalKey = fileUploader.moveFile(tempKey, targetDir);
                String finalUrl = fileUploader.getFileUrl(finalKey);
                updatedContent = updatedContent.replace(tempUrl, finalUrl);
            } catch (Exception e) {
                log.warn("이미지 이동 실패, 스킵: {}", tempKey, e);
            }
        }
        return updatedContent;
    }
}
