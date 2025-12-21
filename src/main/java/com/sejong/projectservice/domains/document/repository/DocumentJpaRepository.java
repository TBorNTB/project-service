package com.sejong.projectservice.domains.document.repository;

import com.sejong.projectservice.domains.document.domain.DocumentEntity;
import feign.Param;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DocumentJpaRepository extends JpaRepository<DocumentEntity, Long> {
    Optional<DocumentEntity> findDocumentEntityById(Long id);

    @Query("SELECT d FROM DocumentEntity d WHERE d.id = :documentId AND d.projectEntity.id = :projectId")
    Optional<DocumentEntity> findByIdAndProjectId(@Param("documentId") Long documentId,
                                                  @Param("projectId") Long projectId);
}
