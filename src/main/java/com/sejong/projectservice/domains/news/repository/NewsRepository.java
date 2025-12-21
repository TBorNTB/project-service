package com.sejong.projectservice.domains.news.repository;

import com.sejong.projectservice.support.common.pagination.CursorPageRequest;
import com.sejong.projectservice.support.common.pagination.CursorPageResponse;
import com.sejong.projectservice.support.common.pagination.CustomPageRequest;
import com.sejong.projectservice.support.common.pagination.OffsetPageResponse;
import com.sejong.projectservice.domains.news.domain.News;

import java.util.List;

public interface NewsRepository {

    News save(News news);

    boolean existsNews(Long newsId);

    News findBy(Long newsId);

    News update(News news);

    void delete(News news);

    OffsetPageResponse<List<News>> findAllWithOffset(CustomPageRequest customPageRequest);

    CursorPageResponse<List<News>> findAllWithCursor(CursorPageRequest cursorPageRequest);

    Long getNewsCount();
}
