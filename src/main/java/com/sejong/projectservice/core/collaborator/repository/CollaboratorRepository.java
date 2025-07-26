package com.sejong.projectservice.core.collaborator.repository;

public interface CollaboratorRepository {
    boolean existsByDocumentYorkieIdAndUsername(String yorkieDocId, String username);
}
