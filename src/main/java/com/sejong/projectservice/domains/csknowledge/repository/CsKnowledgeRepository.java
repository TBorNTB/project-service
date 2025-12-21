package com.sejong.projectservice.domains.csknowledge.repository;

import com.sejong.projectservice.support.common.pagination.CursorPageRequest;
import com.sejong.projectservice.support.common.pagination.CursorPageResponse;
import com.sejong.projectservice.support.common.pagination.CustomPageRequest;
import com.sejong.projectservice.support.common.pagination.OffsetPageResponse;
import com.sejong.projectservice.domains.csknowledge.enums.TechCategory;
import com.sejong.projectservice.domains.csknowledge.domain.CsKnowledge;

import java.util.List;
import java.util.Optional;

public interface CsKnowledgeRepository {
    // CRUD operations
    CsKnowledge save(CsKnowledge csKnowledge);
    
    CsKnowledge findById(Long id);
    
    boolean existsById(Long id);
    
    CsKnowledge update(CsKnowledge csKnowledge);
    
    void delete(CsKnowledge csKnowledge);
    
    // Query operations
    List<CsKnowledge> findAllByTechCategory(TechCategory techCategory);
    
    Optional<CsKnowledge> findUnsentKnowledge(TechCategory categoryName, String email);
    
    // Pagination
    OffsetPageResponse<List<CsKnowledge>> findAllWithOffset(CustomPageRequest customPageRequest);
    
    CursorPageResponse<List<CsKnowledge>> findAllWithCursor(CursorPageRequest cursorPageRequest);

    Long getCsCount();
}
