package com.sejong.archiveservice.core.csknowledge;

import com.sejong.archiveservice.core.common.pagination.CursorPageRequest;
import com.sejong.archiveservice.core.common.pagination.CursorPageResponse;
import com.sejong.archiveservice.core.common.pagination.CustomPageRequest;
import com.sejong.archiveservice.core.common.pagination.OffsetPageResponse;
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
