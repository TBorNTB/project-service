package com.sejong.archiveservice.infrastructure.csknowledge;

import com.sejong.archiveservice.application.exception.BaseException;
import com.sejong.archiveservice.application.exception.ExceptionType;
import com.sejong.archiveservice.core.common.pagination.CursorPageRequest;
import com.sejong.archiveservice.core.common.pagination.CursorPageResponse;
import com.sejong.archiveservice.core.common.pagination.CustomPageRequest;
import com.sejong.archiveservice.core.common.pagination.OffsetPageResponse;
import com.sejong.archiveservice.core.csknowledge.CsKnowledge;
import com.sejong.archiveservice.core.csknowledge.CsKnowledgeRepository;
import com.sejong.archiveservice.core.csknowledge.TechCategory;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CsKnowledgeRepositoryImpl implements CsKnowledgeRepository {

    private final CsKnowledgeJpaRepository repository;

    @Override
    public CsKnowledge save(CsKnowledge csKnowledge) {
        CsKnowledgeEntity entity = CsKnowledgeEntity.from(csKnowledge);
        CsKnowledgeEntity saved = repository.save(entity);
        return saved.toDomain();
    }

    @Override
    public CsKnowledge findById(Long id) {
        CsKnowledgeEntity entity = repository.findById(id)
                .orElseThrow(() -> new BaseException(ExceptionType.NOT_FOUND));
        return entity.toDomain();
    }

    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    @Override
    public CsKnowledge update(CsKnowledge csKnowledge) {
        CsKnowledgeEntity entity = CsKnowledgeEntity.from(csKnowledge);
        CsKnowledgeEntity updated = repository.save(entity);
        return updated.toDomain();
    }

    @Override
    public void delete(CsKnowledge csKnowledge) {
        repository.deleteById(csKnowledge.getId());
    }

    @Override
    public List<CsKnowledge> findAllByTechCategory(TechCategory techCategory) {
        return repository
                .findAllByTechCategory(techCategory).stream()
                .map(CsKnowledgeEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<CsKnowledge> findUnsentKnowledge(TechCategory categoryName, String email) {
        Optional<CsKnowledgeEntity> randomUnsent = repository.findRandomUnsent(categoryName.name(), email);
        return randomUnsent.map(CsKnowledgeEntity::toDomain);
    }

    @Override
    public OffsetPageResponse<List<CsKnowledge>> findAllWithOffset(CustomPageRequest customPageRequest) {
        Pageable pageable = PageRequest.of(
                customPageRequest.getPage(),
                customPageRequest.getSize(),
                Direction.valueOf(customPageRequest.getDirection().name()),
                customPageRequest.getSortBy()
        );

        Page<CsKnowledgeEntity> page = repository.findAll(pageable);

        List<CsKnowledge> knowledges = page.stream()
                .map(CsKnowledgeEntity::toDomain)
                .toList();

        return OffsetPageResponse.ok(page.getNumber(), page.getTotalPages(), knowledges);
    }

    @Override
    public CursorPageResponse<List<CsKnowledge>> findAllWithCursor(CursorPageRequest cursorPageRequest) {
        Pageable pageable = PageRequest.of(0, cursorPageRequest.getSize() + 1);

        List<CsKnowledgeEntity> entities;
        if (cursorPageRequest.getCursor() == null) {
            entities = repository.findAll(pageable).getContent();
        } else {
            entities = repository.findByIdGreaterThan(cursorPageRequest.getCursor(), pageable);
        }

        boolean hasNext = entities.size() > cursorPageRequest.getSize();

        List<CsKnowledge> knowledges = entities.stream()
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