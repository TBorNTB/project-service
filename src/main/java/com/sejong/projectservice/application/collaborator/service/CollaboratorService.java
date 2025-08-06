package com.sejong.projectservice.application.collaborator.service;

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
    private final CollaboratorRepository collaboratorRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public List<Collaborator> updateProject(String userId, Long projectId, List<String> collaboratorNames) {
        //todo collaborator 가 실제 존재하는 이름들인지 feign 적용해야 된다.
        Project project = projectRepository.findOne(projectId);
        project.updateCollaborator(collaboratorNames);
        Project updatedProject = projectRepository.updateCollaborator(project);
        return updatedProject.getCollaborators();
    }
}
