package com.sejong.projectservice.domains.project.service;

import com.sejong.projectservice.domains.csknowledge.service.CsKnowledgeService;
import com.sejong.projectservice.domains.news.service.NewsService;
import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import com.sejong.projectservice.domains.project.dto.request.DateCountRequest;
import com.sejong.projectservice.domains.project.dto.request.ProjectFormRequest;
import com.sejong.projectservice.domains.project.dto.request.ProjectUpdateRequest;
import com.sejong.projectservice.domains.project.dto.response.DateCountResponse;
import com.sejong.projectservice.domains.project.dto.response.ProjectAddResponse;
import com.sejong.projectservice.domains.project.dto.response.ProjectDeleteResponse;
import com.sejong.projectservice.domains.project.dto.response.ProjectPageResponse;
import com.sejong.projectservice.domains.project.dto.response.ProjectSpecifyInfo;
import com.sejong.projectservice.domains.project.dto.response.ProjectUpdateResponse;
import com.sejong.projectservice.domains.project.kafka.dto.ProjectCreatedEventDto;
import com.sejong.projectservice.domains.project.kafka.dto.ProjectDeletedEventDto;
import com.sejong.projectservice.domains.project.kafka.dto.ProjectUpdatedEventDto;
import com.sejong.projectservice.domains.project.repository.ProjectRepository;
import com.sejong.projectservice.domains.project.util.ProjectUsernamesExtractor;
import com.sejong.projectservice.support.common.constants.ProjectStatus;
import com.sejong.projectservice.support.common.exception.BaseException;
import com.sejong.projectservice.support.common.exception.ExceptionType;
import com.sejong.projectservice.support.common.file.FileUploader;
import com.sejong.projectservice.support.common.internal.UserExternalService;
import com.sejong.projectservice.support.common.internal.response.PostLikeCheckResponse;
import com.sejong.projectservice.support.common.internal.response.UserNameInfo;
import com.sejong.projectservice.support.common.util.Mapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final UserExternalService userExternalService;
    private final ProjectRepository projectRepository;
    private final Mapper mapper;
    private final ApplicationEventPublisher eventPublisher;
    private final NewsService newsService;
    private final CsKnowledgeService csKnowledgeService;
    private final FileUploader fileUploader;

    @Transactional
    public ProjectAddResponse createProject(ProjectFormRequest projectFormRequest, String username) {
        userExternalService.validateExistence(username, projectFormRequest.getCollaborators());
        ProjectEntity projectEntity = ProjectEntity.of(projectFormRequest, username);
        ProjectEntity savedProject = projectRepository.save(projectEntity);
        mapper.connectJoins(savedProject, projectFormRequest);

        // 썸네일 파일 처리 (temp → 최종 위치)
        if (projectFormRequest.getThumbnailKey() != null && !projectFormRequest.getThumbnailKey().isEmpty()) {
            String targetDir = String.format("project-service/project/%d/thumbnail", savedProject.getId());
            String finalKey = fileUploader.moveFile(projectFormRequest.getThumbnailKey(), targetDir);
            savedProject.updateThumbnail(finalKey);
        }

        // 에디터 본문 이미지 처리 (temp → 최종 위치) 및 content key 치환
        if (projectFormRequest.getContentImageKeys() != null && !projectFormRequest.getContentImageKeys().isEmpty()) {
            String updatedContent = processContentImages(
                    savedProject.getId(),
                    projectFormRequest.getContent(),
                    projectFormRequest.getContentImageKeys()
            );
            savedProject.updateContent(updatedContent);
        }

        eventPublisher.publishEvent(ProjectCreatedEventDto.of(savedProject.getId()));
        return ProjectAddResponse.from(savedProject.getId(), savedProject.getTitle(), "저장 완료",
                savedProject.getContent(), savedProject.getEndedAt());
    }

    @Transactional
    public ProjectUpdateResponse update(Long projectId, ProjectUpdateRequest projectUpdateRequest, String username) {
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException(ExceptionType.PROJECT_NOT_FOUND));
        project.validateUserPermission(username);

        project.update(projectUpdateRequest.getTitle(), projectUpdateRequest.getDescription(),
                projectUpdateRequest.getProjectStatus(),
                projectUpdateRequest.getThumbnailUrl());

        // 새 썸네일이 전달된 경우 (temp key)
        if (projectUpdateRequest.getThumbnailKey() != null && !projectUpdateRequest.getThumbnailKey().isEmpty()) {
            // 기존 썸네일 삭제
            if (project.getThumbnailKey() != null) {
                try {
                    fileUploader.delete(project.getThumbnailKey());
                } catch (Exception e) {
                    log.warn("기존 썸네일 삭제 실패, 계속 진행: {}", project.getThumbnailKey(), e);
                }
            }
            // 새 썸네일 이동
            String targetDir = String.format("project-service/project/%d/thumbnail", project.getId());
            String finalKey = fileUploader.moveFile(projectUpdateRequest.getThumbnailKey(), targetDir);
            project.updateThumbnail(finalKey);
        }

        // 새 에디터 이미지가 전달된 경우
        if (projectUpdateRequest.getContentImageKeys() != null && !projectUpdateRequest.getContentImageKeys()
                .isEmpty()) {
            String contentToUpdate = projectUpdateRequest.getContent() != null
                    ? projectUpdateRequest.getContent()
                    : project.getContent();
            String updatedContent = processContentImages(
                    project.getId(),
                    contentToUpdate,
                    projectUpdateRequest.getContentImageKeys()
            );
            project.updateContent(updatedContent);
        } else if (projectUpdateRequest.getContent() != null) {
            project.updateContent(projectUpdateRequest.getContent());
        }

        ProjectEntity savedProject = projectRepository.save(project);
        eventPublisher.publishEvent(ProjectUpdatedEventDto.of(savedProject.getId()));
        return ProjectUpdateResponse.from(savedProject.getId(), savedProject.getTitle(), "수정 완료");
    }

    @Transactional
    public ProjectDeleteResponse removeProject(String username, Long projectId, String userRole) {
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException(ExceptionType.PROJECT_NOT_FOUND));
        project.validateOwner(username, userRole);
        projectRepository.deleteById(projectId);
        eventPublisher.publishEvent(ProjectDeletedEventDto.of(projectId));
        return ProjectDeleteResponse.of(project.getTitle(), "삭제 완료");
    }

    @Transactional(readOnly = true)
    public ProjectPageResponse getAllProjects(Pageable pageable) {

        Page<ProjectEntity> projectEntityPage = projectRepository.findAll(pageable);
        List<String> usernames = ProjectUsernamesExtractor.extract(projectEntityPage.getContent());

        Map<String, UserNameInfo> usernamesMap = userExternalService.getUserNameInfos(usernames);
        return ProjectPageResponse.from(projectEntityPage, usernamesMap, fileUploader);
    }

    @Transactional(readOnly = true)
    public ProjectPageResponse search(String keyword, ProjectStatus status, Pageable pageable) {
        Page<ProjectEntity> projectEntitiesPage = projectRepository.searchWithFilters(keyword, status, pageable);
        List<String> usernames = ProjectUsernamesExtractor.extract(projectEntitiesPage.getContent());

        Map<String, UserNameInfo> usernamesMap = userExternalService.getUserNameInfos(usernames);
        return ProjectPageResponse.from(projectEntitiesPage, usernamesMap, fileUploader);
    }

    @Transactional(readOnly = true)
    public ProjectSpecifyInfo findOne(Long projectId) {
        ProjectEntity projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException(ExceptionType.PROJECT_NOT_FOUND));

        List<String> usernames = ProjectUsernamesExtractor.extract(projectEntity);

        Map<String, UserNameInfo> usernamesMap = userExternalService.getUserNameInfos(usernames);
        return ProjectSpecifyInfo.from(projectEntity, usernamesMap, fileUploader);
    }

    @Transactional(readOnly = true)
    public PostLikeCheckResponse checkPost(Long postId) {
        boolean exists = projectRepository.existsById(postId);
        if (exists) {
            ProjectEntity project = projectRepository.findById(postId)
                    .orElseThrow(() -> new BaseException(ExceptionType.PROJECT_NOT_FOUND));
            return PostLikeCheckResponse.hasOfProject(project, true);
        }

        return PostLikeCheckResponse.hasNotOf();
    }

    @Transactional(readOnly = true)
    public boolean exists(Long postId) {
        return projectRepository.existsById(postId);
    }

    @Transactional(readOnly = true)
    public Long getProjectCount() {
        Long count = projectRepository.getProjectCount();
        return count;
    }

    @Transactional(readOnly = true)
    public DateCountResponse getCountsByDate(DateCountRequest request) {
        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();

        Long csCount = csKnowledgeService.getCsCountByDate(startDate, endDate);
        Long newsCount = newsService.getNewsCountByDate(startDate, endDate);
        Long projectCount = projectRepository.getProjectCountByDate(startDateTime, endDateTime);

        return DateCountResponse.of(csCount, newsCount, projectCount);
    }

    @Transactional(readOnly = true)
    public List<Long> getProjectIdsByUsername(String username) {
        return projectRepository.findProjectIdsByUsername(username);
    }

    /**
     * 에디터 본문 이미지를 temp에서 최종 위치로 이동하고 content 내 URL 치환
     */
    private String processContentImages(Long projectId, String content, List<String> imageKeys) {
        String updatedContent = content;
        String targetDir = String.format("project-service/project/%d/images", projectId);

        for (String tempKey : imageKeys) {
            if (tempKey == null || tempKey.isEmpty()) {
                continue;
            }

            try {
                String tempUrl = fileUploader.getFileUrl(tempKey);
                String finalKey = fileUploader.moveFile(tempKey, targetDir);
                String finalUrl = fileUploader.getFileUrl(finalKey);
                updatedContent = updatedContent.replace(tempUrl, finalUrl);
            } catch (Exception e) {
                log.warn("이미지 이동 실패, 스킵: {}", tempKey, e);
            }
        }
        return updatedContent;
    }
}
