package com.sejong.archiveservice.infrastructure.news.repository;

import com.sejong.archiveservice.application.exception.BaseException;
import com.sejong.archiveservice.application.exception.ExceptionType;
import com.sejong.archiveservice.core.common.pagination.CursorPageRequest;
import com.sejong.archiveservice.core.common.pagination.CursorPageResponse;
import com.sejong.archiveservice.core.common.pagination.CustomPageRequest;
import com.sejong.archiveservice.core.common.pagination.OffsetPageResponse;
import com.sejong.archiveservice.core.news.News;
import com.sejong.archiveservice.core.news.NewsRepository;
import com.sejong.archiveservice.infrastructure.news.entity.NewsEntity;
import com.sejong.archiveservice.infrastructure.news.mapper.NewsMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NewsRepositoryImpl implements NewsRepository {
    private final ArchiveJpaRepository archiveJpaRepository;

    @Override
    public News save(News news) {
        NewsEntity entity = NewsMapper.toEntity(news);
        NewsEntity newsEntity = archiveJpaRepository.save(entity);
        return NewsMapper.toDomain(newsEntity);
    }

    @Override
    public boolean existsNews(Long newsId) {
        return archiveJpaRepository.existsById(newsId);
    }

    @Override
    public News findBy(Long newsId) {
        NewsEntity newsEntity = archiveJpaRepository.findById(newsId)
                .orElseThrow(() -> new BaseException(ExceptionType.NEWS_NOT_FOUND));
        return NewsMapper.toDomain(newsEntity);
    }

    @Override
    public News update(News news) {
        NewsEntity entity = NewsMapper.toEntity(news);
        return NewsMapper.toDomain(archiveJpaRepository.save(entity));
    }


    @Override
    public void delete(News news) {
        archiveJpaRepository.deleteById(news.getId());
    }

    @Override
    public OffsetPageResponse<List<News>> findAllWithOffset(CustomPageRequest customPageRequest) {
        Pageable pageable = PageRequest.of(customPageRequest.getPage(),
                customPageRequest.getSize(),
                Direction.valueOf(customPageRequest.getDirection().name()),
                customPageRequest.getSortBy());

        Page<NewsEntity> archiveEntities = archiveJpaRepository.findAll(pageable);

        List<News> archives = archiveEntities.stream()
                .map(NewsMapper::toDomain)
                .toList();

        return OffsetPageResponse.ok(archiveEntities.getNumber(), archiveEntities.getTotalPages(), archives);
    }

    @Override
    public CursorPageResponse<List<News>> findAllWithCursor(CursorPageRequest cursorPageRequest) {
        Pageable pageable = PageRequest.of(0, cursorPageRequest.getSize() + 1); // +1로 다음 페이지 존재 여부 확인

        List<NewsEntity> entities = getCursorBasedEntities(cursorPageRequest, pageable);

        // 실제 요청한 크기보다 많이 조회되면 다음 페이지가 존재
        boolean hasNext = entities.size() > cursorPageRequest.getSize();

        // 실제 반환할 데이터는 요청한 크기만큼만
        List<NewsEntity> resultEntities = hasNext ?
                entities.subList(0, cursorPageRequest.getSize()) : entities; // Todo: 아예 sql로 limit

        List<News> news = resultEntities.stream()
                .map(NewsMapper::toDomain)
                .toList();

        // 다음 커서 계산
        Long nextCursor = hasNext && !news.isEmpty() ?
                news.get(news.size() - 1).getId() : null;

        return CursorPageResponse.ok(nextCursor, hasNext, news);
    }

    @Override
    public Long getNewsCount() {
        return archiveJpaRepository.getNewsCount();
    }

    private List<NewsEntity> getCursorBasedEntities(CursorPageRequest request, Pageable pageable) {
        boolean isDesc = request.getDirection() == CursorPageRequest.SortDirection.DESC;

        if (request.getCursor() == null) {
            // 첫 페이지
            return isDesc ?
                    archiveJpaRepository.findFirstPageDesc(pageable) :
                    archiveJpaRepository.findFirstPageAsc(pageable);
        } else {
            // 커서 기반 페이지
            return isDesc ?
                    archiveJpaRepository.findByCursorDesc(request.getCursor(), pageable) :
                    archiveJpaRepository.findByCursorAsc(request.getCursor(), pageable);
        }
    }
}
