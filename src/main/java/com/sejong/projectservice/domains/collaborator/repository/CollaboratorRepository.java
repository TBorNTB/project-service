package com.sejong.projectservice.domains.collaborator.repository;

public interface CollaboratorRepository {
    boolean existsByDocumentYorkieIdAndUsername(String yorkieDocId, String username);
}
