package com.sejong.projectservice.domains.csknowledge.service;


import com.sejong.projectservice.domains.category.domain.CategoryEntity;
import com.sejong.projectservice.domains.category.repository.CategoryRepository;
import com.sejong.projectservice.domains.csknowledge.domain.CsKnowledgeEntity;
import com.sejong.projectservice.domains.csknowledge.repository.CsKnowledgeRepository;
import com.sejong.projectservice.domains.csknowledge.util.CsKnowledgeAssembler;
import com.sejong.projectservice.domains.csknowledge.dto.CsKnowledgeReqDto;
import com.sejong.projectservice.domains.csknowledge.dto.CsKnowledgeResDto;
import com.sejong.projectservice.support.common.error.code.ErrorCode;
import com.sejong.projectservice.support.common.error.exception.ApiException;
import com.sejong.projectservice.support.common.exception.BaseException;
import com.sejong.projectservice.support.common.exception.ExceptionType;
import com.sejong.projectservice.support.common.pagination.CursorPageReqDto;
import com.sejong.projectservice.support.common.pagination.OffsetPageReqDto;
import com.sejong.projectservice.support.common.internal.UserExternalService;
import com.sejong.projectservice.support.common.internal.response.PostLikeCheckResponse;
import com.sejong.projectservice.support.common.internal.response.UserNameInfo;
import com.sejong.projectservice.support.common.util.ExtractorUsername;
import com.sejong.projectservice.support.common.pagination.CursorPageRequest;
import com.sejong.projectservice.support.common.pagination.CursorPageResponse;
import com.sejong.projectservice.support.common.pagination.CustomPageRequest;
import com.sejong.projectservice.support.common.pagination.OffsetPageResponse;
import com.sejong.projectservice.domains.csknowledge.domain.CsKnowledgeDto;
import com.sejong.projectservice.domains.csknowledge.kafka.CsKnowledgeEventPublisher;
import lombok.RequiredArgsConstructor;
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
public class CsKnowledgeService {

    private final CsKnowledgeEventPublisher csKnowledgeEventPublisher;
    private final UserExternalService userExternalService;
    private final CsKnowledgeRepository csKnowledgeRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public CsKnowledgeResDto createCsKnowledge(CsKnowledgeReqDto csKnowledgeReqDto, String username) {
        userExternalService.validateExistence(username);
        CategoryEntity categoryEntity = categoryRepository.findByName(csKnowledgeReqDto.category())
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST));
        CsKnowledgeDto csKnowledgeDto = CsKnowledgeAssembler.toCsKnowledge(csKnowledgeReqDto, username);
        CsKnowledgeEntity entity = CsKnowledgeEntity.from(csKnowledgeDto, categoryEntity);
        CsKnowledgeEntity savedEntity = csKnowledgeRepository.save(entity);
        CsKnowledgeDto dto = savedEntity.toDto();

        CsKnowledgeResDto response = resolveUsername(dto);
        csKnowledgeEventPublisher.publishCreated(dto);
        return response;
    }

    @Transactional
    public CsKnowledgeResDto updateCsKnowledge(Long csKnowledgeId, CsKnowledgeReqDto csKnowledgeReqDto, String username) {
        CsKnowledgeEntity csKnowledgeEntity = csKnowledgeRepository.findById(csKnowledgeId)
                .orElseThrow(() -> new BaseException(ExceptionType.NOT_FOUND));
        CategoryEntity categoryEntity = categoryRepository.findByName(csKnowledgeReqDto.category())
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST));

        csKnowledgeEntity.validateOwnerPermission(username);
        csKnowledgeEntity.update(csKnowledgeReqDto,LocalDateTime.now(),username,categoryEntity);

        CsKnowledgeDto dto = csKnowledgeEntity.toDto();

        CsKnowledgeResDto response = resolveUsername(dto);
        csKnowledgeEventPublisher.publishUpdated(dto);
        return response;
    }

    @Transactional
    public void deleteCsKnowledge(Long csKnowledgeId, String username, String userRole) {
        CsKnowledgeEntity csKnowledgeEntity = csKnowledgeRepository.findById(csKnowledgeId)
                .orElseThrow(() -> new BaseException(ExceptionType.NOT_FOUND));
        csKnowledgeEntity.validateOwnerPermission(username, userRole);
        csKnowledgeRepository.deleteById(csKnowledgeEntity.getId());
        csKnowledgeEventPublisher.publishDeleted(csKnowledgeId);
    }

    public CsKnowledgeResDto findById(Long csKnowledgeId) {
        CsKnowledgeEntity csKnowledgeEntity = csKnowledgeRepository.findById(csKnowledgeId)
                .orElseThrow(() -> new BaseException(ExceptionType.NOT_FOUND));
        return resolveUsername(csKnowledgeEntity.toDto());
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
                    .orElseThrow(() -> new BaseException(ExceptionType.NOT_FOUND));
            return PostLikeCheckResponse.hasOfCS(csKnowledgeEntity.toDto(), true);
        }

        return PostLikeCheckResponse.hasNotOf();
    }

    public List<CsKnowledgeResDto> findAllByTechCategory(String categoryName) {
        List<CsKnowledgeDto> csKnowledgeDtos = csKnowledgeRepository
                .findAllByCategoryEntity_Name(categoryName).stream()
                .map(CsKnowledgeEntity::toDto)
                .toList();
        return resolveUsernames(csKnowledgeDtos);
    }

    public Optional<CsKnowledgeResDto> findUnsentKnowledge(String categoryName, String email) {

        Optional<CsKnowledgeEntity> randomUnsent = csKnowledgeRepository.findRandomUnsent(categoryName, email);
        Optional<CsKnowledgeDto> csKnowledgeDto = randomUnsent.map(CsKnowledgeEntity::toDto);
        return csKnowledgeDto
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

        List<CsKnowledgeDto> csKnowledgeDtos = page.stream()
                .map(CsKnowledgeEntity::toDto)
                .toList();

        OffsetPageResponse<List<CsKnowledgeDto>> csKnowledges = OffsetPageResponse.ok(page.getNumber(), page.getTotalPages(), csKnowledgeDtos);

        List<CsKnowledgeResDto> csKnowledgeResDtoList = resolveUsernames(csKnowledges.getData());

        return OffsetPageResponse.ok(
                csKnowledges.getPage(),
                csKnowledges.getTotalPage(),
                csKnowledgeResDtoList
        );
    }

    public CursorPageResponse<List<CsKnowledgeResDto>> getCursorCsKnowledge(CursorPageReqDto cursorPageReqDto) {
        CursorPageRequest pageRequest = cursorPageReqDto.toPageRequest();
        Pageable pageable = PageRequest.of(0, pageRequest.getSize() + 1);

        List<CsKnowledgeEntity> csKnowledgeEntities;
        if (pageRequest.getCursor() == null) {
            csKnowledgeEntities = csKnowledgeRepository.findAll(pageable).getContent();
        } else {
            csKnowledgeEntities = csKnowledgeRepository.findByIdGreaterThan(pageRequest.getCursor().getProjectId(), pageable);
        }

        boolean hasNext = csKnowledgeEntities.size() > pageRequest.getSize();

        List<CsKnowledgeDto> knowledges = csKnowledgeEntities.stream()
                .limit(pageRequest.getSize())
                .map(CsKnowledgeEntity::toDto)
                .toList();

        Long nextCursor = hasNext && !knowledges.isEmpty()
                ? knowledges.get(knowledges.size() - 1).getId()
                : null;

        CursorPageResponse<List<CsKnowledgeDto>> csKnowledges = CursorPageResponse.ok(nextCursor, hasNext, knowledges);

        List<CsKnowledgeResDto> csKnowledgeResDtoList = resolveUsernames(csKnowledges.getContent());

        return CursorPageResponse.ok(
                csKnowledges.getNextCursor(),
                csKnowledges.isHasNext(),
                csKnowledgeResDtoList
        );
    }

    private CsKnowledgeResDto resolveUsername(CsKnowledgeDto csKnowledgeDto) {
        List<String> usernames = ExtractorUsername.FromKnowledge(csKnowledgeDto);
        Map<String, UserNameInfo> usernamesMap = userExternalService.getUserNameInfos(usernames);
        return CsKnowledgeResDto.from(csKnowledgeDto, usernamesMap.get(csKnowledgeDto.getWriterId().userId()).nickname());
    }

    private List<CsKnowledgeResDto> resolveUsernames(List<CsKnowledgeDto> csKnowledgeDtos) {
        List<String> usernames = ExtractorUsername.FromKnowledges(csKnowledgeDtos);
        Map<String, UserNameInfo> usernamesMap = userExternalService.getUserNameInfos(usernames);
        return csKnowledgeDtos.stream()
                .map(cs -> CsKnowledgeResDto.from(cs, usernamesMap.get(cs.getWriterId().userId()).nickname()))
                .toList();
    }

    @Transactional(readOnly = true)
    public Long getCsCount() {
        return csKnowledgeRepository.getCsCount();
    }
}
