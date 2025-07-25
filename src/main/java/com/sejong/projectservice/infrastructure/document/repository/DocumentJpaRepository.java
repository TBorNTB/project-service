package com.sejong.projectservice.infrastructure.document.repository;

import com.sejong.projectservice.core.document.domain.Document;

public interface DocumentJpaRepository {
    public Document save(Document document);
}
