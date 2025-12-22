package com.sejong.projectservice.domains.project.util;

import com.sejong.projectservice.domains.collaborator.domain.CollaboratorEntity;
import com.sejong.projectservice.domains.project.domain.ProjectEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ProjectUsernamesExtractor {

    public static List<String> extract(List<ProjectEntity> projects) {
        return projects.stream()
                .flatMap(project -> Stream.concat(
                        Stream.of(project.getUsername()),
                        project.getCollaborators().stream()
                                .map(CollaboratorEntity::getCollaboratorName)
                ))
                .toList();
    }

    public static List<String> extract(ProjectEntity projectEntity) {
        List<String> usernames = new ArrayList<>();
        usernames.add(projectEntity.getUsername());
        projectEntity.getCollaborators()
                .forEach(it->usernames.add(it.getCollaboratorName()));
        return usernames;
    }
}
