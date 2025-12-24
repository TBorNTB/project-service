package com.sejong.projectservice.domains.collaborator.service;

import com.sejong.projectservice.support.common.exception.BaseException;
import com.sejong.projectservice.support.common.exception.ExceptionType;
import com.sejong.projectservice.support.common.internal.UserExternalService;
import com.sejong.projectservice.domains.collaborator.dto.CollaboratorDto;
import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import com.sejong.projectservice.domains.project.repository.ProjectRepository;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CollaboratorService {

    private final UserExternalService userExternalService;
    private final ProjectRepository projectRepository;

    @Transactional
    public List<CollaboratorDto> updateProject(String username, Long projectId, List<String> collaboratorNames) {
        userExternalService.validateExistence(username, collaboratorNames);

        ProjectEntity projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException(ExceptionType.PROJECT_NOT_FOUND));
        projectEntity.validateUserPermission(username);
        projectEntity.updateCollaborator(collaboratorNames);
        return CollaboratorDto.toDtoList(projectEntity.getCollaboratorEntities());
    }
}
