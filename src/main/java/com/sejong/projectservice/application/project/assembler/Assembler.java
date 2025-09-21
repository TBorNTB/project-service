package com.sejong.projectservice.application.project.assembler;

import com.sejong.projectservice.application.document.dto.DocumentCreateReq;
import com.sejong.projectservice.application.project.dto.request.ProjectFormRequest;
import com.sejong.projectservice.core.category.Category;
import com.sejong.projectservice.core.collaborator.domain.Collaborator;
import com.sejong.projectservice.core.document.domain.Document;
import com.sejong.projectservice.core.project.domain.Project;
import com.sejong.projectservice.core.subgoal.SubGoal;
import com.sejong.projectservice.core.techstack.TechStack;
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

    public static Project toProject(ProjectFormRequest request, String userNickname) {
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
                .userNickname(userNickname)
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
