package com.sejong.projectservice.application.project.service;

import com.sejong.projectservice.core.collaborator.domain.Collaborator;
import com.sejong.projectservice.core.project.domain.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ProjectUsernamesExtractor {

    public static List<String> extract(List<Project> projects) {
        return projects.stream()
                .flatMap(project -> Stream.concat(
                        Stream.of(project.getUsername()),
                        project.getCollaborators().stream()
                                .map(Collaborator::getCollaboratorName)
                ))
                .toList();
    }

    public static List<String> extract(Project project) {
        List<String> usernames = new ArrayList<>();
        usernames.add(project.getUsername());
        project.getCollaborators()
                .forEach(it->usernames.add(it.getCollaboratorName()));
        return usernames;
    }
}
