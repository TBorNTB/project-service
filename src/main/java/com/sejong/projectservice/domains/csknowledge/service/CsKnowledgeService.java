package com.sejong.projectservice.domains.csknowledge.service;


import com.sejong.projectservice.domains.csknowledge.util.CsKnowledgeAssembler;
import com.sejong.projectservice.domains.csknowledge.dto.CsKnowledgeReqDto;
import com.sejong.projectservice.domains.csknowledge.dto.CsKnowledgeResDto;
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
import com.sejong.projectservice.domains.csknowledge.domain.CsKnowledge;
import com.sejong.projectservice.domains.csknowledge.repository.CsKnowledgeRepository;
import com.sejong.projectservice.domains.csknowledge.enums.TechCategory;
import com.sejong.projectservice.domains.csknowledge.kafka.CsKnowledgeEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CsKnowledgeService {

    private final CsKnowledgeRepository csKnowledgeRepository;
    private final CsKnowledgeEventPublisher csKnowledgeEventPublisher;
    private final UserExternalService userExternalService;

    @Transactional
    public CsKnowledgeResDto createCsKnowledge(CsKnowledgeReqDto csKnowledgeReqDto, String username) {
        userExternalService.validateExistence(username);
        CsKnowledge csKnowledge = CsKnowledgeAssembler.toCsKnowledge(csKnowledgeReqDto, username);
        CsKnowledge savedCsKnowledge = csKnowledgeRepository.save(csKnowledge);

        CsKnowledgeResDto response = resolveUsername(savedCsKnowledge);
        csKnowledgeEventPublisher.publishCreated(savedCsKnowledge);
        return response;
    }

    @Transactional
    public CsKnowledgeResDto updateCsKnowledge(Long csKnowledgeId, CsKnowledgeReqDto csKnowledgeReqDto, String username) {
        CsKnowledge existingKnowledge = csKnowledgeRepository.findById(csKnowledgeId);
        existingKnowledge.validateOwnerPermission(username);

        CsKnowledge updatedKnowledge = CsKnowledgeAssembler.toCsKnowledgeForUpdate(
                csKnowledgeId,
                csKnowledgeReqDto,
                existingKnowledge.getCreatedAt(),
                username
        );
        CsKnowledge savedKnowledge = csKnowledgeRepository.update(updatedKnowledge);

        CsKnowledgeResDto response = resolveUsername(savedKnowledge);
        csKnowledgeEventPublisher.publishUpdated(savedKnowledge);
        return response;
    }

    @Transactional
    public void deleteCsKnowledge(Long csKnowledgeId, String username, String userRole) {
        CsKnowledge csKnowledge = csKnowledgeRepository.findById(csKnowledgeId);
        csKnowledge.validateOwnerPermission(username, userRole);
        csKnowledgeRepository.delete(csKnowledge);
        csKnowledgeEventPublisher.publishDeleted(csKnowledgeId);
    }

    public CsKnowledgeResDto findById(Long csKnowledgeId) {
        CsKnowledge csKnowledge = csKnowledgeRepository.findById(csKnowledgeId);
        return resolveUsername(csKnowledge);
    }

    @Transactional(readOnly = true)
    public Boolean exists(Long csKnowledgeId) {
        return csKnowledgeRepository.existsById(csKnowledgeId);
    }

    @Transactional(readOnly = true)
    public PostLikeCheckResponse checkCS(Long csKnowledgeId) {
        boolean exists = csKnowledgeRepository.existsById(csKnowledgeId);
        if(exists){
            CsKnowledge csKnowledge = csKnowledgeRepository.findById(csKnowledgeId);
            return PostLikeCheckResponse.hasOfCS(csKnowledge, true);
        }

        return PostLikeCheckResponse.hasNotOf();
    }

    public List<CsKnowledgeResDto> findAllByTechCategory(TechCategory techCategory) {
        List<CsKnowledge> csKnowledges = csKnowledgeRepository.findAllByTechCategory(techCategory);
        return resolveUsernames(csKnowledges);
    }

    public Optional<CsKnowledgeResDto> findUnsentKnowledge(TechCategory categoryName, String email) {
        return csKnowledgeRepository.findUnsentKnowledge(categoryName, email)
                .map(this::resolveUsername);
    }

    public OffsetPageResponse<List<CsKnowledgeResDto>> getOffsetCsKnowledge(OffsetPageReqDto offsetPageReqDto) {
        CustomPageRequest pageRequest = offsetPageReqDto.toPageRequest();
        OffsetPageResponse<List<CsKnowledge>> csKnowledges = csKnowledgeRepository.findAllWithOffset(pageRequest);

        List<CsKnowledgeResDto> csKnowledgeResDtoList = resolveUsernames(csKnowledges.getData());

        return OffsetPageResponse.ok(
                csKnowledges.getPage(),
                csKnowledges.getTotalPage(),
                csKnowledgeResDtoList
        );
    }

    public CursorPageResponse<List<CsKnowledgeResDto>> getCursorCsKnowledge(CursorPageReqDto cursorPageReqDto) {
        CursorPageRequest pageRequest = cursorPageReqDto.toPageRequest();
        CursorPageResponse<List<CsKnowledge>> csKnowledges = csKnowledgeRepository.findAllWithCursor(pageRequest);

        List<CsKnowledgeResDto> csKnowledgeResDtoList = resolveUsernames(csKnowledges.getContent());

        return CursorPageResponse.ok(
                csKnowledges.getNextCursor(),
                csKnowledges.isHasNext(),
                csKnowledgeResDtoList
        );
    }

    private CsKnowledgeResDto resolveUsername(CsKnowledge csKnowledge) {
        List<String> usernames = ExtractorUsername.FromKnowledge(csKnowledge);
        Map<String, UserNameInfo> usernamesMap = userExternalService.getUserNameInfos(usernames);
        return CsKnowledgeResDto.from(csKnowledge, usernamesMap.get(csKnowledge.getWriterId().userId()).nickname());
    }

    private List<CsKnowledgeResDto> resolveUsernames(List<CsKnowledge> csKnowledges) {
        List<String> usernames = ExtractorUsername.FromKnowledges(csKnowledges);
        Map<String, UserNameInfo> usernamesMap = userExternalService.getUserNameInfos(usernames);
        return csKnowledges.stream()
                .map(cs -> CsKnowledgeResDto.from(cs, usernamesMap.get(cs.getWriterId().userId()).nickname()))
                .toList();
    }

    @Transactional(readOnly = true)
    public Long getCsCount() {
        return csKnowledgeRepository.getCsCount();
    }
}
