package com.sejong.projectservice.domains.project.util;

import com.sejong.projectservice.domains.collaborator.domain.Collaborator;
import com.sejong.projectservice.domains.project.domain.Project;

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
