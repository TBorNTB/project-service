package com.sejong.projectservice.support.common.util;

import org.owasp.html.AttributePolicy;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * XSS 방어용 유틸.
 * - 일반 텍스트: HTML 이스케이프 (title, description 등)
 * - 본문(HTML): OWASP 라이브러리 기본 정책 조합 + 체크리스트(input/label/data-type) 허용
 */
public final class XssSanitizer {

    /** input type 속성: checkbox만 허용 (XSS 방지) */
    private static final AttributePolicy INPUT_TYPE_CHECKBOX_ONLY = (elementName, attributeName, value) ->
            "checkbox".equalsIgnoreCase(value) ? "checkbox" : null;

    /** 체크리스트/태스크리스트용: input[type=checkbox], label, data-type 허용 (TipTap 등 에디터 호환) */
    private static final PolicyFactory CHECKLIST_POLICY = new HtmlPolicyBuilder()
            .allowElements("input", "label")
            .allowAttributes("type").matching(INPUT_TYPE_CHECKBOX_ONLY).onElements("input")
            .allowAttributes("checked").onElements("input")
            .allowAttributes("data-type").onElements("ul", "ol", "li", "label", "div", "span")
            .toFactory();

    /** 라이브러리 제공 정책 + 체크리스트 정책. */
    private static final PolicyFactory RICH_TEXT_POLICY = Sanitizers.FORMATTING
            .and(Sanitizers.BLOCKS)
            .and(Sanitizers.LINKS)
            .and(Sanitizers.IMAGES)
            .and(CHECKLIST_POLICY);

    private XssSanitizer() {
    }

    /**
     * 일반 텍스트 필드용 (title, description, summary 등) - HTML 특수문자 이스케이프
     */
    public static String escape(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return HtmlUtils.htmlEscape(input);
    }

    /**
     * 본문(HTML) 필드용 - 위험 태그/속성 제거, 안전한 태그만 허용
     */
    public static String sanitizeHtml(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return RICH_TEXT_POLICY.sanitize(input);
    }

    /**
     * 리스트 내 각 항목 이스케이프 (categories, tags, subGoals 등)
     */
    public static List<String> escapeList(List<String> input) {
        if (input == null) {
            return null;
        }
        return input.stream()
                .map(XssSanitizer::escape)
                .collect(Collectors.toList());
    }
}
