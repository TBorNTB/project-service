package com.sejong.projectservice.core.project.domain;

import com.sejong.projectservice.core.category.Category;
import com.sejong.projectservice.core.collaborator.domain.Collaborator;
import com.sejong.projectservice.core.document.domain.Document;
import com.sejong.projectservice.core.enums.ProjectStatus;
import com.sejong.projectservice.core.subgoal.SubGoal;
import com.sejong.projectservice.core.techstack.TechStack;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Project {

    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

    private Long id;
    private List<Collaborator> collaborators = new ArrayList<>();

    private String title;
    private String description;
    private ProjectStatus projectStatus;
    private String thumbnailUrl;

    private List<SubGoal> subGoals = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();
    private List<TechStack> techStacks = new ArrayList<>();
    private List<Document> documents = new ArrayList<>();

    public void update(String title, String description,
                       ProjectStatus projectStatus, String thumbnailUrl) {
        this.title = title;
        this.description = description;
        this.projectStatus = projectStatus;
        this.thumbnailUrl = thumbnailUrl;
        this.updatedAt = LocalDateTime.now();
    }

    public void addDocument(Document doc) {
        documents.add(doc);
    }
}
