package com.sejong.projectservice.domains.collaborator.service;

import com.sejong.projectservice.client.UserExternalService;
import com.sejong.projectservice.domains.collaborator.domain.CollaboratorDto;
import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import com.sejong.projectservice.domains.project.repository.ProjectJpaRepository;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CollaboratorService {

    private final UserExternalService userExternalService;
    private final ProjectJpaRepository projectJpaRepository;

    @Transactional
    public List<CollaboratorDto> updateProject(String username, Long projectId, List<String> collaboratorNames) {
        userExternalService.validateExistence(username, collaboratorNames);

        ProjectEntity projectEntity = projectJpaRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        projectEntity.validateUserPermission(username);
        projectEntity.updateCollaborator(collaboratorNames);
        return CollaboratorDto.toDtoList(projectEntity.getCollaborators());
    }
}
