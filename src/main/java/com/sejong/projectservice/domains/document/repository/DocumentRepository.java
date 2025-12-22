package com.sejong.projectservice.domains.document.repository;

import com.sejong.projectservice.domains.document.domain.DocumentDto;

public interface DocumentRepository {
    DocumentDto findByIdAndProjectId(Long documentId, Long projectId);

    DocumentDto findById(Long documentId);

    DocumentDto save(DocumentDto documentDto);

    void delete(DocumentDto documentDto);
}
