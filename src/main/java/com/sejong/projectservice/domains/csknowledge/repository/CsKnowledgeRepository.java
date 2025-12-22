package com.sejong.projectservice.domains.csknowledge.repository;

import com.sejong.projectservice.support.common.pagination.CursorPageRequest;
import com.sejong.projectservice.support.common.pagination.CursorPageResponse;
import com.sejong.projectservice.support.common.pagination.CustomPageRequest;
import com.sejong.projectservice.support.common.pagination.OffsetPageResponse;
import com.sejong.projectservice.domains.csknowledge.enums.TechCategory;
import com.sejong.projectservice.domains.csknowledge.domain.CsKnowledgeDto;

import java.util.List;
import java.util.Optional;

public interface CsKnowledgeRepository {
    // CRUD operations
    CsKnowledgeDto save(CsKnowledgeDto csKnowledgeDto);
    
    CsKnowledgeDto findById(Long id);
    
    boolean existsById(Long id);
    
    CsKnowledgeDto update(CsKnowledgeDto csKnowledgeDto);
    
    void delete(CsKnowledgeDto csKnowledgeDto);
    
    // Query operations
    List<CsKnowledgeDto> findAllByTechCategory(TechCategory techCategory);
    
    Optional<CsKnowledgeDto> findUnsentKnowledge(TechCategory categoryName, String email);
    
    // Pagination
    OffsetPageResponse<List<CsKnowledgeDto>> findAllWithOffset(CustomPageRequest customPageRequest);
    
    CursorPageResponse<List<CsKnowledgeDto>> findAllWithCursor(CursorPageRequest cursorPageRequest);

    Long getCsCount();
}
