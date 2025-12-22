package com.sejong.projectservice.domains.csknowledge.repository;


import com.sejong.projectservice.support.common.exception.BaseException;
import com.sejong.projectservice.support.common.exception.ExceptionType;
import com.sejong.projectservice.support.common.pagination.CursorPageRequest;
import com.sejong.projectservice.support.common.pagination.CursorPageResponse;
import com.sejong.projectservice.support.common.pagination.CustomPageRequest;
import com.sejong.projectservice.support.common.pagination.OffsetPageResponse;
import com.sejong.projectservice.domains.csknowledge.domain.CsKnowledgeDto;
import com.sejong.projectservice.domains.csknowledge.domain.CsKnowledgeEntity;
import com.sejong.projectservice.domains.csknowledge.enums.TechCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CsKnowledgeRepositoryImpl implements CsKnowledgeRepository {

    private final CsKnowledgeJpaRepository repository;

    @Override
    public CsKnowledgeDto save(CsKnowledgeDto csKnowledgeDto) {
        CsKnowledgeEntity entity = CsKnowledgeEntity.from(csKnowledgeDto);
        CsKnowledgeEntity saved = repository.save(entity);
        return saved.toDomain();
    }

    @Override
    public CsKnowledgeDto findById(Long id) {
        CsKnowledgeEntity entity = repository.findById(id)
                .orElseThrow(() -> new BaseException(ExceptionType.NOT_FOUND));
        return entity.toDomain();
    }

    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    @Override
    public CsKnowledgeDto update(CsKnowledgeDto csKnowledgeDto) {
        CsKnowledgeEntity entity = CsKnowledgeEntity.from(csKnowledgeDto);
        CsKnowledgeEntity updated = repository.save(entity);
        return updated.toDomain();
    }

    @Override
    public void delete(CsKnowledgeDto csKnowledgeDto) {
        repository.deleteById(csKnowledgeDto.getId());
    }

    @Override
    public List<CsKnowledgeDto> findAllByTechCategory(TechCategory techCategory) {
        return repository
                .findAllByTechCategory(techCategory).stream()
                .map(CsKnowledgeEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<CsKnowledgeDto> findUnsentKnowledge(TechCategory categoryName, String email) {
        Optional<CsKnowledgeEntity> randomUnsent = repository.findRandomUnsent(categoryName.name(), email);
        return randomUnsent.map(CsKnowledgeEntity::toDomain);
    }

    @Override
    public OffsetPageResponse<List<CsKnowledgeDto>> findAllWithOffset(CustomPageRequest customPageRequest) {
        Pageable pageable = PageRequest.of(
                customPageRequest.getPage(),
                customPageRequest.getSize(),
                Direction.valueOf(customPageRequest.getDirection().name()),
                customPageRequest.getSortBy()
        );

        Page<CsKnowledgeEntity> page = repository.findAll(pageable);

        List<CsKnowledgeDto> knowledges = page.stream()
                .map(CsKnowledgeEntity::toDomain)
                .toList();

        return OffsetPageResponse.ok(page.getNumber(), page.getTotalPages(), knowledges);
    }

    @Override
    public CursorPageResponse<List<CsKnowledgeDto>> findAllWithCursor(CursorPageRequest cursorPageRequest) {
        Pageable pageable = PageRequest.of(0, cursorPageRequest.getSize() + 1);

        List<CsKnowledgeEntity> entities;
        if (cursorPageRequest.getCursor() == null) {
            entities = repository.findAll(pageable).getContent();
        } else {
            entities = repository.findByIdGreaterThan(cursorPageRequest.getCursor().getProjectId(), pageable);
        }

        boolean hasNext = entities.size() > cursorPageRequest.getSize();

        List<CsKnowledgeDto> knowledges = entities.stream()
                .limit(cursorPageRequest.getSize())
                .map(CsKnowledgeEntity::toDomain)
                .toList();

        Long nextCursor = hasNext && !knowledges.isEmpty()
                ? knowledges.get(knowledges.size() - 1).getId()
                : null;

        return CursorPageResponse.ok(nextCursor, hasNext, knowledges);
    }

    @Override
    public Long getCsCount() {
        return repository.getCsCount();
    }
}