package com.sejong.projectservice.core.news;

import com.sejong.projectservice.core.common.pagination.CursorPageRequest;
import com.sejong.projectservice.core.common.pagination.CursorPageResponse;
import com.sejong.projectservice.core.common.pagination.CustomPageRequest;
import com.sejong.projectservice.core.common.pagination.OffsetPageResponse;

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
