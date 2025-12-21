package com.sejong.projectservice.domains.project.util;

import com.sejong.projectservice.domains.document.dto.DocumentCreateReq;
import com.sejong.projectservice.domains.project.dto.request.ProjectFormRequest;
import com.sejong.projectservice.client.response.UserNameInfo;
import com.sejong.projectservice.domains.category.domain.Category;
import com.sejong.projectservice.domains.collaborator.domain.Collaborator;
import com.sejong.projectservice.domains.document.domain.Document;
import com.sejong.projectservice.domains.project.domain.Project;
import com.sejong.projectservice.domains.subgoal.domain.SubGoal;
import com.sejong.projectservice.domains.techstack.domain.TechStack;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Assembler {
    public static Document toDocument(DocumentCreateReq request, String yorkieDocumentId, Long projectId) {
        return Document.builder()
                .id(null)
                .yorkieDocumentId(yorkieDocumentId)
                .title(request.getTitle())
                .content(request.getContent())
                .description(request.getDescription())
                .thumbnailUrl(request.getThumbnailUrl())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .projectId(projectId)
                .build();
    }

    public static Project toProject(ProjectFormRequest request, String username, UserNameInfo userNameInfo) {
        List<Collaborator> collaborators = request.getCollaborators().stream()
                .map(Collaborator::from)
                .toList();

        List<TechStack> techStacks = request.getTechStacks().stream()
                .map(TechStack::of)
                .toList();

        List<Category> categories = request.getCategories().stream()
                .map(Category::of)
                .toList();

        List<SubGoal> subGoals = request.getSubGoals().stream()
                .map(it -> SubGoal.from(it, false, LocalDateTime.now(), LocalDateTime.now()))
                .toList();

        return Project.builder()
                .title(request.getTitle())
                .username(username)
                .nickname(userNameInfo.nickname())
                .realname(userNameInfo.nickname())
                .description(request.getDescription())
                .categories(categories)
                .projectStatus(request.getProjectStatus())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .thumbnailUrl(request.getThumbnail())
                .techStacks(techStacks)
                .collaborators(collaborators)
                .subGoals(subGoals)
                .documents(new ArrayList<>())
                .build();
    }
}
