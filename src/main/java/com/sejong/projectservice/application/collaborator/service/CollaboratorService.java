package com.sejong.projectservice.application.collaborator.service;

import com.sejong.projectservice.application.internal.UserExternalService;
import com.sejong.projectservice.core.collaborator.domain.Collaborator;
import com.sejong.projectservice.core.collaborator.repository.CollaboratorRepository;
import com.sejong.projectservice.core.project.domain.Project;
import com.sejong.projectservice.core.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CollaboratorService {
    private final ProjectRepository projectRepository;
    private final UserExternalService userExternalService;

    @Transactional
    public List<Collaborator> updateProject(String userId, Long projectId, List<String> collaboratorNames) {
        userExternalService.validateExistence(collaboratorNames);

        Project project = projectRepository.findOne(projectId);
        project.validateOwner(Long.valueOf(userId));
        project.updateCollaborator(collaboratorNames);
        Project updatedProject = projectRepository.updateCollaborator(project);
        return updatedProject.getCollaborators();
    }
}
