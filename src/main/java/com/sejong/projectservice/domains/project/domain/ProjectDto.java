package com.sejong.projectservice.domains.project.domain;

import com.sejong.projectservice.domains.collaborator.domain.CollaboratorDto;
import com.sejong.projectservice.domains.document.domain.DocumentDto;
import com.sejong.projectservice.domains.subgoal.domain.SubGoalDto;
import com.sejong.projectservice.support.common.error.code.ErrorCode;
import com.sejong.projectservice.support.common.error.exception.ApiException;
import com.sejong.projectservice.domains.category.domain.CategoryDto;
import com.sejong.projectservice.domains.enums.ProjectStatus;
import com.sejong.projectservice.domains.techstack.domain.TechStackDto;
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
