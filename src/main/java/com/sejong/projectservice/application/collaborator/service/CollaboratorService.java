package com.sejong.projectservice.application.collaborator.service;

import com.sejong.projectservice.client.UserExternalService;
import com.sejong.projectservice.core.collaborator.domain.Collaborator;
import com.sejong.projectservice.core.project.domain.Project;
import com.sejong.projectservice.core.project.repository.ProjectRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CollaboratorService {
    private final ProjectRepository projectRepository;
    private final UserExternalService userExternalService;

    @Transactional
    public List<Collaborator> updateProject(String username, Long projectId, List<String> collaboratorNames) {
        userExternalService.validateExistence(username, collaboratorNames);

        Project project = projectRepository.findOne(projectId);
        project.validateUserPermission(username);
        project.updateCollaborator(collaboratorNames);
        Project updatedProject = projectRepository.updateCollaborator(project);
        return updatedProject.getCollaborators();
    }
}
