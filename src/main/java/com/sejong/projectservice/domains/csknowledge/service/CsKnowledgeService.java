package com.sejong.projectservice.domains.csknowledge.service;


import com.sejong.projectservice.domains.category.domain.CategoryEntity;
import com.sejong.projectservice.domains.category.repository.CategoryRepository;
import com.sejong.projectservice.domains.csknowledge.domain.CsKnowledgeEntity;
import com.sejong.projectservice.domains.csknowledge.kafka.dto.CsKnowledgeCreatedEventDto;
import com.sejong.projectservice.domains.csknowledge.kafka.dto.CsKnowledgeDeletedEventDto;
import com.sejong.projectservice.domains.csknowledge.kafka.dto.CsKnowledgeUpdatedEventDto;
import com.sejong.projectservice.domains.csknowledge.repository.CsKnowledgeRepository;
import com.sejong.projectservice.domains.csknowledge.dto.CsKnowledgeReqDto;
import com.sejong.projectservice.domains.csknowledge.dto.CsKnowledgeResDto;
import com.sejong.projectservice.support.common.exception.BaseException;
import com.sejong.projectservice.support.common.exception.ExceptionType;
import com.sejong.projectservice.support.common.file.FileUploader;
import com.sejong.projectservice.support.common.pagination.Cursor;
import com.sejong.projectservice.support.common.pagination.CursorPageReqDto;
import com.sejong.projectservice.support.common.pagination.OffsetPageReqDto;
import com.sejong.projectservice.support.common.internal.UserExternalService;
import com.sejong.projectservice.support.common.internal.response.PostLikeCheckResponse;
import com.sejong.projectservice.support.common.internal.response.UserNameInfo;
import com.sejong.projectservice.support.common.util.ExtractorUsername;
import com.sejong.projectservice.support.common.pagination.CursorPageRequest;
import com.sejong.projectservice.support.common.pagination.CursorPageRes;
import com.sejong.projectservice.support.common.pagination.CustomPageRequest;
import com.sejong.projectservice.support.common.pagination.OffsetPageResponse;
import com.sejong.projectservice.support.common.pagination.enums.SortDirection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CsKnowledgeService {

    private final UserExternalService userExternalService;
    private final CsKnowledgeRepository csKnowledgeRepository;
    private final CategoryRepository categoryRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final FileUploader fileUploader;

    @Transactional
    public CsKnowledgeResDto createCsKnowledge(CsKnowledgeReqDto csKnowledgeReqDto, String username) {
        userExternalService.validateExistence(username);
        CategoryEntity categoryEntity = categoryRepository.findByName(csKnowledgeReqDto.category())
                .orElseThrow(() -> new BaseException(ExceptionType.CATEGORY_NOT_FOUND));
        CsKnowledgeEntity csKnowledgeEntity = CsKnowledgeEntity.of(
                csKnowledgeReqDto.title(),
                csKnowledgeReqDto.content(),
                username,
                categoryEntity,
                LocalDateTime.now()
        );
        CsKnowledgeEntity savedEntity = csKnowledgeRepository.save(csKnowledgeEntity);

        // 에디터 본문 이미지 처리 (temp → 최종 위치) 및 content key 치환
        if (csKnowledgeReqDto.contentImageKeys() != null && !csKnowledgeReqDto.contentImageKeys().isEmpty()) {
            String updatedContent = processContentImages(
                    savedEntity.getId(),
                    csKnowledgeReqDto.content(),
                    csKnowledgeReqDto.contentImageKeys()
            );
            savedEntity.updateContent(updatedContent);
        }

        CsKnowledgeResDto response = resolveUsername(savedEntity);
        applicationEventPublisher.publishEvent(CsKnowledgeCreatedEventDto.of(savedEntity.getId()));
        return response;
    }

    @Transactional
    public CsKnowledgeResDto updateCsKnowledge(Long csKnowledgeId, CsKnowledgeReqDto csKnowledgeReqDto, String username) {
        CsKnowledgeEntity csKnowledgeEntity = csKnowledgeRepository.findById(csKnowledgeId)
                .orElseThrow(() -> new BaseException(ExceptionType.CS_KNOWLEDGE_NOT_FOUND));
        CategoryEntity categoryEntity = categoryRepository.findByName(csKnowledgeReqDto.category())
                .orElseThrow(() -> new BaseException(ExceptionType.CATEGORY_NOT_FOUND));

        csKnowledgeEntity.validateOwnerPermission(username);
        csKnowledgeEntity.update(
                csKnowledgeReqDto.title(),
                csKnowledgeReqDto.content(),
                username,
                categoryEntity,
                LocalDateTime.now()
        );

        // 새 에디터 이미지가 전달된 경우
        if (csKnowledgeReqDto.contentImageKeys() != null && !csKnowledgeReqDto.contentImageKeys().isEmpty()) {
            String updatedContent = processContentImages(
                    csKnowledgeEntity.getId(),
                    csKnowledgeEntity.getContent(),
                    csKnowledgeReqDto.contentImageKeys()
            );
            csKnowledgeEntity.updateContent(updatedContent);
        }

        CsKnowledgeResDto response = resolveUsername(csKnowledgeEntity);
        applicationEventPublisher.publishEvent(CsKnowledgeUpdatedEventDto.of(csKnowledgeEntity.getId()));
        return response;
    }

    @Transactional
    public void deleteCsKnowledge(Long csKnowledgeId, String username, String userRole) {
        CsKnowledgeEntity csKnowledgeEntity = csKnowledgeRepository.findById(csKnowledgeId)
                .orElseThrow(() -> new BaseException(ExceptionType.CS_KNOWLEDGE_NOT_FOUND));
        csKnowledgeEntity.validateOwnerPermission(username, userRole);
        csKnowledgeRepository.deleteById(csKnowledgeEntity.getId());
        applicationEventPublisher.publishEvent(CsKnowledgeDeletedEventDto.of(csKnowledgeEntity.getId()));
    }

    public CsKnowledgeResDto findById(Long csKnowledgeId) {
        CsKnowledgeEntity csKnowledgeEntity = csKnowledgeRepository.findById(csKnowledgeId)
                .orElseThrow(() -> new BaseException(ExceptionType.CS_KNOWLEDGE_NOT_FOUND));
        return resolveUsername(csKnowledgeEntity);
    }

    @Transactional(readOnly = true)
    public Boolean exists(Long csKnowledgeId) {
        return csKnowledgeRepository.existsById(csKnowledgeId);
    }

    @Transactional(readOnly = true)
    public PostLikeCheckResponse checkCS(Long csKnowledgeId) {
        boolean exists = csKnowledgeRepository.existsById(csKnowledgeId);
        if (exists) {
            CsKnowledgeEntity csKnowledgeEntity = csKnowledgeRepository.findById(csKnowledgeId)
                    .orElseThrow(() -> new BaseException(ExceptionType.CS_KNOWLEDGE_NOT_FOUND));
            return PostLikeCheckResponse.hasOfCS(csKnowledgeEntity, true);
        }

        return PostLikeCheckResponse.hasNotOf();
    }

    public List<CsKnowledgeResDto> findAllByTechCategory(String categoryName) {
        List<CsKnowledgeEntity> csKnowledgeEntities = csKnowledgeRepository
                .findAllByCategoryEntity_Name(categoryName);
        return resolveUsernames(csKnowledgeEntities);
    }

    public Optional<CsKnowledgeResDto> findUnsentKnowledge(String categoryName, String email) {

        Optional<CsKnowledgeEntity> randomUnsent = csKnowledgeRepository.findRandomUnsent(categoryName, email);
        return randomUnsent
                .map(this::resolveUsername);
    }

    public OffsetPageResponse<List<CsKnowledgeResDto>> getOffsetCsKnowledge(OffsetPageReqDto offsetPageReqDto) {
        CustomPageRequest pageRequest = offsetPageReqDto.toPageRequest();

        Pageable pageable = PageRequest.of(
                pageRequest.getPage(),
                pageRequest.getSize(),
                Sort.Direction.valueOf(pageRequest.getDirection().name()),
                pageRequest.getSortBy()
        );

        Page<CsKnowledgeEntity> page = csKnowledgeRepository.findAll(pageable);

        List<CsKnowledgeResDto> csKnowledgeResDtoList = resolveUsernames(page.getContent());

        return OffsetPageResponse.ok(
                page.getNumber(),
                page.getTotalPages(),
                csKnowledgeResDtoList
        );
    }

    public CursorPageRes<List<CsKnowledgeResDto>> getCursorCsKnowledge(CursorPageReqDto cursorPageReqDto) {
        CursorPageRequest pageRequest = cursorPageReqDto.toPageRequest();
        Pageable pageable = PageRequest.of(0, pageRequest.getSize() + 1);

        List<CsKnowledgeEntity> csKnowledgeEntities = getCursorBasedEntities(pageRequest, pageable);

        List<CsKnowledgeResDto> csKnowledgeResDtoList = resolveUsernames(csKnowledgeEntities);

        return CursorPageRes.from(
                csKnowledgeResDtoList,
                pageRequest.getSize(),
                CsKnowledgeResDto::id
        );
    }

    private List<CsKnowledgeEntity> getCursorBasedEntities(CursorPageRequest request, Pageable pageable) {
        boolean isDesc = request.getDirection() == SortDirection.DESC;

        if (request.getCursor() == null) {
            // 첫 페이지
            return isDesc ?
                    csKnowledgeRepository.findFirstPageDesc(pageable) :
                    csKnowledgeRepository.findFirstPageAsc(pageable);
        } else {
            // 커서 기반 페이지
            return isDesc ?
                    csKnowledgeRepository.findByCursorDesc(request.getCursor().getProjectId(), pageable) :
                    csKnowledgeRepository.findByCursorAsc(request.getCursor().getProjectId(), pageable);
        }
    }

    private CsKnowledgeResDto resolveUsername(CsKnowledgeEntity csKnowledgeEntity) {
        List<String> usernames = ExtractorUsername.FromKnowledge(csKnowledgeEntity);
        Map<String, UserNameInfo> usernamesMap = userExternalService.getUserNameInfos(usernames);
        return CsKnowledgeResDto.from(csKnowledgeEntity, usernamesMap.get(csKnowledgeEntity.getWriterId()).nickname());
    }

    private List<CsKnowledgeResDto> resolveUsernames(List<CsKnowledgeEntity> csKnowledgeEntities) {
        List<String> usernames = ExtractorUsername.FromKnowledges(csKnowledgeEntities);
        Map<String, UserNameInfo> usernamesMap = userExternalService.getUserNameInfos(usernames);
        return csKnowledgeEntities.stream()
                .map(cs -> CsKnowledgeResDto.from(cs, usernamesMap.get(cs.getWriterId()).nickname()))
                .toList();
    }

    @Transactional(readOnly = true)
    public Long getCsCount() {
        return csKnowledgeRepository.getCsCount();
    }

    @Transactional(readOnly = true)
    public Long getCsCountByDate(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
        return csKnowledgeRepository.getCsCountByDate(startDateTime, endDateTime);
    }

    @Transactional(readOnly = true)
    public List<Long> getCsKnowledgeIdsByUsername(String username) {
        return csKnowledgeRepository.findCsKnowledgeIdsByUsername(username);
    }

    /**
     * 에디터 본문 이미지를 temp에서 최종 위치로 이동하고 content 내 URL 치환
     */
    private String processContentImages(Long csKnowledgeId, String content, List<String> imageKeys) {
        String updatedContent = content;
        String targetDir = String.format("project-service/cs-knowledge/%d/images", csKnowledgeId);

        for (String tempKey : imageKeys) {
            if (tempKey == null || tempKey.isEmpty()) continue;

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
