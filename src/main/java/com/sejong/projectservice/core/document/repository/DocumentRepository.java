package com.sejong.projectservice.core.document.repository;

import com.sejong.projectservice.core.document.domain.Document;

public interface DocumentRepository {
    Document findByDocumentId(Long documentId);
}
