package com.sejong.projectservice.support.common.sanitizer;

import com.sejong.projectservice.domains.csknowledge.dto.CsKnowledgeReqDto;
import com.sejong.projectservice.domains.news.dto.NewsReqDto;
import com.sejong.projectservice.domains.project.dto.request.ProjectFormRequest;
import com.sejong.projectservice.domains.project.dto.request.ProjectUpdateRequest;
import com.sejong.projectservice.support.common.util.XssSanitizer;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * XSS 방어 정책의 기본 구현.
 * Sanitize 대상/방식 변경 시 이 클래스만 수정하면 됨.
 */
@Component
public class DefaultRequestSanitizer implements RequestSanitizer {

    @Override
    public void sanitize(ProjectFormRequest request) {
        if (request.getTitle() != null) request.setTitle(XssSanitizer.escape(request.getTitle()));
        if (request.getDescription() != null) request.setDescription(XssSanitizer.escape(request.getDescription()));
        if (request.getContent() != null) request.setContent(XssSanitizer.sanitizeHtml(request.getContent()));
        if (request.getSubGoals() != null) request.setSubGoals(XssSanitizer.escapeList(request.getSubGoals()));
        if (request.getCategories() != null) request.setCategories(XssSanitizer.escapeList(request.getCategories()));
        if (request.getTechStacks() != null) request.setTechStacks(XssSanitizer.escapeList(request.getTechStacks()));
        if (request.getCollaborators() != null) request.setCollaborators(XssSanitizer.escapeList(request.getCollaborators()));
    }

    @Override
    public void sanitize(ProjectUpdateRequest request) {
        if (request.getTitle() != null) request.setTitle(XssSanitizer.escape(request.getTitle()));
        if (request.getDescription() != null) request.setDescription(XssSanitizer.escape(request.getDescription()));
        if (request.getContent() != null) request.setContent(XssSanitizer.sanitizeHtml(request.getContent()));
    }

    @Override
    public SanitizedCsKnowledge sanitize(CsKnowledgeReqDto request) {
        return new SanitizedCsKnowledge(
                XssSanitizer.escape(request.title()),
                XssSanitizer.sanitizeHtml(request.content()),
                XssSanitizer.escape(request.description()),
                XssSanitizer.escape(request.category())
        );
    }

    @Override
    public void sanitize(NewsReqDto request) {
        if (request.getTitle() != null) request.setTitle(XssSanitizer.escape(request.getTitle()));
        if (request.getSummary() != null) request.setSummary(XssSanitizer.escape(request.getSummary()));
        if (request.getContent() != null) request.setContent(XssSanitizer.sanitizeHtml(request.getContent()));
        if (request.getCategory() != null) request.setCategory(XssSanitizer.escape(request.getCategory()));
        if (request.getTags() != null) request.setTags(XssSanitizer.escapeList(request.getTags()));
    }

    @Override
    public SanitizedQuestionInput sanitizeQuestionInput(String title, String description, String content, List<String> categories) {
        return new SanitizedQuestionInput(
                XssSanitizer.escape(title),
                XssSanitizer.escape(description),
                XssSanitizer.sanitizeHtml(content),
                categories != null ? XssSanitizer.escapeList(categories) : null
        );
    }

    @Override
    public String sanitizeAnswerContent(String content) {
        return XssSanitizer.sanitizeHtml(content);
    }
}
