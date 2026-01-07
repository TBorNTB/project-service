package com.sejong.projectservice.domains.project.dto;

import com.sejong.projectservice.domains.collaborator.dto.CollaboratorDto;
import com.sejong.projectservice.domains.document.dto.DocumentDto;
import com.sejong.projectservice.domains.subgoal.dto.SubGoalDto;
import com.sejong.projectservice.domains.category.dto.CategoryDto;
import com.sejong.projectservice.support.common.constants.ProjectStatus;
import com.sejong.projectservice.domains.techstack.dto.TechStackDto;
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
public class ProjectDto {

    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

    private Long id;
    private String username;
    private String nickname;
    private String realname;
    private List<CollaboratorDto> collaboratorDtos = new ArrayList<>();

    private String title;
    private String description;
    private ProjectStatus projectStatus;
    private String thumbnailUrl;

    private List<SubGoalDto> subGoalDtos = new ArrayList<>();
    private List<CategoryDto> categories = new ArrayList<>();
    private List<TechStackDto> techStackDtos = new ArrayList<>();
    private List<DocumentDto> documentDtos = new ArrayList<>();
}
