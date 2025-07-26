package com.sejong.projectservice.infrastructure.collaborator.repository;

import com.sejong.projectservice.core.collaborator.repository.CollaboratorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CollaboratorRepositoryImpl implements CollaboratorRepository {

    private final CollaboratorJpaRepository collaboratorJpaRepository;

    @Override
    public boolean existsByDocumentYorkieIdAndUsername(String yorkieDocId, String username) {
        return collaboratorJpaRepository.existsByYorkieDocIdAndUsername(yorkieDocId, username);
    }
}
