package com.sejong.projectservice.application.project.controller.fixture;

import static org.mockito.Mockito.mock;

import com.sejong.projectservice.domains.collaborator.domain.CollaboratorDto;
import com.sejong.projectservice.domains.project.domain.ProjectDto;
import com.sejong.projectservice.domains.project.dto.request.ProjectFormRequest;
import com.sejong.projectservice.domains.project.dto.request.ProjectUpdateRequest;
import com.sejong.projectservice.domains.project.dto.response.ProjectAddResponse;
import com.sejong.projectservice.domains.project.dto.response.ProjectPageResponse;
import com.sejong.projectservice.domains.category.domain.CategoryDto;
import com.sejong.projectservice.support.common.constants.ProjectStatus;
import com.sejong.projectservice.domains.subgoal.domain.SubGoalDto;
import com.sejong.projectservice.domains.techstack.domain.TechStackDto;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

public class ProjectFixture {

    public static ProjectFormRequest createProjectFormRequest(String title) {
        return ProjectFormRequest.builder()
                .title(title)
                .createdAt(LocalDateTime.of(2025, 7, 9, 10, 0))
                .description("Spring Boot를 활용한 REST API 서버 개발 프로젝트입니다.")
//                .category(Category.CRYPTOGRAPHY) // 예시: 직접 enum 정의한 값
                .projectStatus(ProjectStatus.IN_PROGRESS)
                .collaborators(List.of("김개발", "이디자인"))
                .techStacks(List.of("Java", "Spring Boot", "MySQL", "Redis"))
                .subGoals(List.of("OAuth2 로그인 구현", "게시판 CRUD", "S3 이미지 업로드"))
                .thumbnail("https://example.com/images/project-thumbnail.png")
//                .contentJson("""
//                            {
//                              "blocks": [
//                                { "type": "header", "data": "프로젝트 소개" },
//                                { "type": "paragraph", "data": "이 프로젝트는 포트폴리오용입니다." }
//                              ]
//                            }
//                        """)
                .build();
    }

    public static ProjectUpdateRequest createProjectUpdateRequest(String title) {
        return ProjectUpdateRequest.builder()
                .title(title)
                .description("설명")
                .projectStatus(ProjectStatus.COMPLETED)
                .thumbnailUrl("https://example.com/images/project-thumbnail.png")
                .build();
    }

    public static ProjectAddResponse createProjectAddResponse(String title) {
        return ProjectAddResponse.from(title, "저장 완료!");
    }

    public static ProjectDto createProject(String title) {
        return ProjectDto.builder()
                .id(1L)
                .title(title)
                .description("Spring Boot를 활용한 REST API 서버 개발 프로젝트입니다.")
//                .category(Category.CRYPTOGRAPHY)
                .projectStatus(ProjectStatus.IN_PROGRESS)
                .createdAt(LocalDateTime.of(2025, 7, 9, 10, 0))
                .updatedAt(LocalDateTime.of(2025, 7, 9, 12, 0))
                .thumbnailUrl("https://example.com/images/project-thumbnail.png")
//                .contentJson("""
//                            {
//                              "blocks": [
//                                { "type": "header", "data": "프로젝트 소개" },
//                                { "type": "paragraph", "data": "이 프로젝트는 포트폴리오용입니다." }
//                              ]
//                            }
//                        """)
//                .userId(userId)
                .categories(List.of(mock(CategoryDto.class), mock(CategoryDto.class)))
                .collaboratorDtos(List.of(mock(CollaboratorDto.class), mock(CollaboratorDto.class)))
                .techStackDtos(List.of(mock(TechStackDto.class), mock(TechStackDto.class)))
                .subGoalDtos(List.of(mock(SubGoalDto.class), mock(SubGoalDto.class)))
                .build();
    }

    public static ProjectPageResponse createProjectPageResponse() {
        List<ProjectDto> projectDtos = List.of(
                createProject("테스트 프로젝트 A"),
                createProject("테스트 프로젝트 B")
        );

        Page<ProjectDto> projectPage = new PageImpl<>(
                projectDtos,
                PageRequest.of(0, 10),
                projectDtos.size()
        );

        return ProjectPageResponse.from(projectPage);
    }
}
