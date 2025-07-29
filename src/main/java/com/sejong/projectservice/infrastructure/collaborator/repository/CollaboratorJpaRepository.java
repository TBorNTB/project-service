package com.sejong.projectservice.infrastructure.collaborator.repository;

import com.sejong.projectservice.infrastructure.project.entity.ProjectEntity;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CollaboratorJpaRepository extends JpaRepository<ProjectEntity, Long> {

    @Query(value = """
            SELECT EXISTS (
                SELECT 1
                FROM project_user pu
                JOIN document d ON pu.project_id = d.project_id
                WHERE d.yorkie_document_id = :yorkieDocId
                  AND pu.username = :username
            )
            """, nativeQuery = true)
    boolean existsByYorkieDocIdAndUsername(@Param("yorkieDocId") String yorkieDocId,
                                           @Param("username") String username);
}
