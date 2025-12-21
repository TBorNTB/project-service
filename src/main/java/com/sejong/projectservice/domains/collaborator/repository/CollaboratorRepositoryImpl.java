package com.sejong.projectservice.domains.collaborator.repository;

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
