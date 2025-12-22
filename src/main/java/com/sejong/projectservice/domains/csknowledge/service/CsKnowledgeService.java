package com.sejong.projectservice.domains.csknowledge.service;


import com.sejong.projectservice.domains.csknowledge.domain.CsKnowledgeEntity;
import com.sejong.projectservice.domains.csknowledge.repository.CsKnowledgeRepository;
import com.sejong.projectservice.domains.csknowledge.util.CsKnowledgeAssembler;
import com.sejong.projectservice.domains.csknowledge.dto.CsKnowledgeReqDto;
import com.sejong.projectservice.domains.csknowledge.dto.CsKnowledgeResDto;
import com.sejong.projectservice.support.common.exception.BaseException;
import com.sejong.projectservice.support.common.exception.ExceptionType;
import com.sejong.projectservice.support.common.pagination.CursorPageReqDto;
import com.sejong.projectservice.support.common.pagination.OffsetPageReqDto;
import com.sejong.projectservice.client.UserExternalService;
import com.sejong.projectservice.client.response.PostLikeCheckResponse;
import com.sejong.projectservice.client.response.UserNameInfo;
import com.sejong.projectservice.support.common.util.ExtractorUsername;
import com.sejong.projectservice.support.common.pagination.CursorPageRequest;
import com.sejong.projectservice.support.common.pagination.CursorPageResponse;
import com.sejong.projectservice.support.common.pagination.CustomPageRequest;
import com.sejong.projectservice.support.common.pagination.OffsetPageResponse;
import com.sejong.projectservice.domains.csknowledge.domain.CsKnowledgeDto;
import com.sejong.projectservice.domains.csknowledge.enums.TechCategory;
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

    @Transactional
    public CsKnowledgeResDto createCsKnowledge(CsKnowledgeReqDto csKnowledgeReqDto, String username) {
        userExternalService.validateExistence(username);
        CsKnowledgeDto csKnowledgeDto = CsKnowledgeAssembler.toCsKnowledge(csKnowledgeReqDto, username);
        CsKnowledgeEntity entity = CsKnowledgeEntity.from(csKnowledgeDto);
        CsKnowledgeEntity savedEntity = csKnowledgeRepository.save(entity);
        CsKnowledgeDto domain = savedEntity.toDomain();

        CsKnowledgeResDto response = resolveUsername(domain);
        csKnowledgeEventPublisher.publishCreated(domain);
        return response;
    }

    @Transactional
    public CsKnowledgeResDto updateCsKnowledge(Long csKnowledgeId, CsKnowledgeReqDto csKnowledgeReqDto, String username) {
        CsKnowledgeEntity entity = csKnowledgeRepository.findById(csKnowledgeId)
                .orElseThrow(() -> new BaseException(ExceptionType.NOT_FOUND));

        entity.validateOwnerPermission(username);
        entity.update(csKnowledgeId,
                csKnowledgeReqDto,
                LocalDateTime.now(),
                username);

        CsKnowledgeDto domain = entity.toDomain();

        CsKnowledgeResDto response = resolveUsername(domain);
        csKnowledgeEventPublisher.publishUpdated(domain);
        return response;
    }

    @Transactional
    public void deleteCsKnowledge(Long csKnowledgeId, String username, String userRole) {
        CsKnowledgeEntity entity = csKnowledgeRepository.findById(csKnowledgeId)
                .orElseThrow(() -> new BaseException(ExceptionType.NOT_FOUND));
        entity.validateOwnerPermission(username, userRole);
        csKnowledgeRepository.deleteById(entity.getId());
        csKnowledgeEventPublisher.publishDeleted(csKnowledgeId);
    }

    public CsKnowledgeResDto findById(Long csKnowledgeId) {
        CsKnowledgeEntity entity = csKnowledgeRepository.findById(csKnowledgeId)
                .orElseThrow(() -> new BaseException(ExceptionType.NOT_FOUND));
        return resolveUsername(entity.toDomain());
    }

    @Transactional(readOnly = true)
    public Boolean exists(Long csKnowledgeId) {
        return csKnowledgeRepository.existsById(csKnowledgeId);
    }

    @Transactional(readOnly = true)
    public PostLikeCheckResponse checkCS(Long csKnowledgeId) {
        boolean exists = csKnowledgeRepository.existsById(csKnowledgeId);
        if (exists) {
            CsKnowledgeEntity entity = csKnowledgeRepository.findById(csKnowledgeId)
                    .orElseThrow(() -> new BaseException(ExceptionType.NOT_FOUND));
            return PostLikeCheckResponse.hasOfCS(entity.toDomain(), true);
        }

        return PostLikeCheckResponse.hasNotOf();
    }

    public List<CsKnowledgeResDto> findAllByTechCategory(TechCategory techCategory) {
        List<CsKnowledgeDto> csKnowledgeDtos = csKnowledgeRepository
                .findAllByTechCategory(techCategory).stream()
                .map(CsKnowledgeEntity::toDomain)
                .toList();
        return resolveUsernames(csKnowledgeDtos);
    }

    public Optional<CsKnowledgeResDto> findUnsentKnowledge(TechCategory categoryName, String email) {

        Optional<CsKnowledgeEntity> randomUnsent = csKnowledgeRepository.findRandomUnsent(categoryName.name(), email);
        Optional<CsKnowledgeDto> csKnowledge = randomUnsent.map(CsKnowledgeEntity::toDomain);
        return csKnowledge
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

        List<CsKnowledgeDto> knowledges = page.stream()
                .map(CsKnowledgeEntity::toDomain)
                .toList();

        OffsetPageResponse<List<CsKnowledgeDto>> csKnowledges = OffsetPageResponse.ok(page.getNumber(), page.getTotalPages(), knowledges);

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

        List<CsKnowledgeEntity> entities;
        if (pageRequest.getCursor() == null) {
            entities = csKnowledgeRepository.findAll(pageable).getContent();
        } else {
            entities = csKnowledgeRepository.findByIdGreaterThan(pageRequest.getCursor().getProjectId(), pageable);
        }

        boolean hasNext = entities.size() > pageRequest.getSize();

        List<CsKnowledgeDto> knowledges = entities.stream()
                .limit(pageRequest.getSize())
                .map(CsKnowledgeEntity::toDomain)
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
