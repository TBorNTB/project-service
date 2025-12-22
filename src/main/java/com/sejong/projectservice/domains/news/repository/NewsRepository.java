package com.sejong.projectservice.domains.news.repository;

import com.sejong.projectservice.support.common.pagination.CursorPageRequest;
import com.sejong.projectservice.support.common.pagination.CursorPageResponse;
import com.sejong.projectservice.support.common.pagination.CustomPageRequest;
import com.sejong.projectservice.support.common.pagination.OffsetPageResponse;
import com.sejong.projectservice.domains.news.domain.NewsDto;

import java.util.List;

public interface NewsRepository {

    NewsDto save(NewsDto newsDto);

    boolean existsNews(Long newsId);

    NewsDto findBy(Long newsId);

    NewsDto update(NewsDto newsDto);

    void delete(NewsDto newsDto);

    OffsetPageResponse<List<NewsDto>> findAllWithOffset(CustomPageRequest customPageRequest);

    CursorPageResponse<List<NewsDto>> findAllWithCursor(CursorPageRequest cursorPageRequest);

    Long getNewsCount();
}
