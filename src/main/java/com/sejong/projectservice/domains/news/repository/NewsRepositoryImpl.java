package com.sejong.projectservice.domains.news.repository;


import com.sejong.projectservice.support.common.exception.BaseException;
import com.sejong.projectservice.support.common.exception.ExceptionType;
import com.sejong.projectservice.support.common.pagination.CursorPageRequest;
import com.sejong.projectservice.support.common.pagination.CursorPageResponse;
import com.sejong.projectservice.support.common.pagination.CustomPageRequest;
import com.sejong.projectservice.support.common.pagination.OffsetPageResponse;
import com.sejong.projectservice.support.common.pagination.enums.SortDirection;
import com.sejong.projectservice.domains.news.domain.NewsDto;
import com.sejong.projectservice.domains.news.domain.NewsEntity;
import com.sejong.projectservice.domains.news.util.NewsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NewsRepositoryImpl implements NewsRepository {
    private final ArchiveJpaRepository archiveJpaRepository;

    @Override
    public NewsDto save(NewsDto newsDto) {
        NewsEntity entity = NewsMapper.toEntity(newsDto);
        NewsEntity newsEntity = archiveJpaRepository.save(entity);
        return NewsMapper.toDomain(newsEntity);
    }

    @Override
    public boolean existsNews(Long newsId) {
        return archiveJpaRepository.existsById(newsId);
    }

    @Override
    public NewsDto findBy(Long newsId) {
        NewsEntity newsEntity = archiveJpaRepository.findById(newsId)
                .orElseThrow(() -> new BaseException(ExceptionType.NEWS_NOT_FOUND));
        return NewsMapper.toDomain(newsEntity);
    }

    @Override
    public NewsDto update(NewsDto newsDto) {
        NewsEntity entity = NewsMapper.toEntity(newsDto);
        return NewsMapper.toDomain(archiveJpaRepository.save(entity));
    }


    @Override
    public void delete(NewsDto newsDto) {
        archiveJpaRepository.deleteById(newsDto.getId());
    }

    @Override
    public OffsetPageResponse<List<NewsDto>> findAllWithOffset(CustomPageRequest customPageRequest) {
        Pageable pageable = PageRequest.of(customPageRequest.getPage(),
                customPageRequest.getSize(),
                Sort.Direction.valueOf(customPageRequest.getDirection().name()),
                customPageRequest.getSortBy());

        Page<NewsEntity> archiveEntities = archiveJpaRepository.findAll(pageable);

        List<NewsDto> archives = archiveEntities.stream()
                .map(NewsMapper::toDomain)
                .toList();

        return OffsetPageResponse.ok(archiveEntities.getNumber(), archiveEntities.getTotalPages(), archives);
    }

    @Override
    public CursorPageResponse<List<NewsDto>> findAllWithCursor(CursorPageRequest cursorPageRequest) {
        Pageable pageable = PageRequest.of(0, cursorPageRequest.getSize() + 1); // +1로 다음 페이지 존재 여부 확인

        List<NewsEntity> entities = getCursorBasedEntities(cursorPageRequest, pageable);

        // 실제 요청한 크기보다 많이 조회되면 다음 페이지가 존재
        boolean hasNext = entities.size() > cursorPageRequest.getSize();

        // 실제 반환할 데이터는 요청한 크기만큼만
        List<NewsEntity> resultEntities = hasNext ?
                entities.subList(0, cursorPageRequest.getSize()) : entities; // Todo: 아예 sql로 limit

        List<NewsDto> newsDtos = resultEntities.stream()
                .map(NewsMapper::toDomain)
                .toList();

        // 다음 커서 계산
        Long nextCursor = hasNext && !newsDtos.isEmpty() ?
                newsDtos.get(newsDtos.size() - 1).getId() : null;

        return CursorPageResponse.ok(nextCursor, hasNext, newsDtos);
    }

    @Override
    public Long getNewsCount() {
        return archiveJpaRepository.getNewsCount();
    }

    private List<NewsEntity> getCursorBasedEntities(CursorPageRequest request, Pageable pageable) {
        boolean isDesc = request.getDirection() == SortDirection.DESC;

        if (request.getCursor() == null) {
            // 첫 페이지
            return isDesc ?
                    archiveJpaRepository.findFirstPageDesc(pageable) :
                    archiveJpaRepository.findFirstPageAsc(pageable);
        } else {
            // 커서 기반 페이지
            return isDesc ?
                    archiveJpaRepository.findByCursorDesc(request.getCursor().getProjectId(), pageable) :
                    archiveJpaRepository.findByCursorAsc(request.getCursor().getProjectId(), pageable);
        }
    }
}
