package com.sejong.projectservice.support.common.util;

import org.owasp.html.AttributePolicy;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * XSS 방어용 유틸.
 * - 일반 텍스트: HTML 이스케이프 (title, description 등)
 * - 본문(HTML): 대부분의 안전한 HTML 허용, script/iframe/on* 등 위험 요소만 차단
 */
public final class XssSanitizer {

    /** input type 속성: checkbox만 허용 (XSS 방지) */
    private static final AttributePolicy INPUT_TYPE_CHECKBOX_ONLY = (elementName, attributeName, value) ->
            "checkbox".equalsIgnoreCase(value) ? "checkbox" : null;

    /**
     * 본문용 HTML 정책: 넓은 허용, 위험한 것만 차단.
     * - 허용: 포맷/블록/코드(pre,code)/테이블/링크/이미지/체크리스트/스타일/class,id,title,data-type
     * - 차단: script, iframe, object, embed, form, on* 이벤트, javascript: 프로토콜 (OWASP 기본 동작)
     */
    private static final PolicyFactory RICH_TEXT_POLICY = new HtmlPolicyBuilder()
            // 공통 포맷/블록 (p, div, h1~h6, ul, ol, li, blockquote, b, i, code, span, br 등)
            .allowCommonInlineFormattingElements()
            .allowCommonBlockElements()
            // 링크/이미지 태그 + 코드 블록, 테이블, 시맨틱/기타 블록
            .allowElements(
                    "a", "img",
                    "pre", "code", "hr",
                    "table", "tr", "td", "th", "thead", "tbody", "tfoot", "col", "colgroup", "caption",
                    "figure", "figcaption", "section", "article", "header", "footer", "nav", "main", "aside",
                    "details", "summary", "mark", "kbd", "samp", "var",
                    "input", "label")
            // 링크/이미지: http, https, mailto만 허용 (javascript: 등 차단)
            .allowStandardUrlProtocols()
            .allowAttributes("href", "rel").onElements("a")
            .requireRelNofollowOnLinks()
            .allowAttributes("src", "alt", "width", "height").onElements("img")
            // 테이블 속성
            .allowAttributes("summary").onElements("table")
            .allowAttributes("align", "valign", "colspan", "rowspan").onElements("table", "tr", "td", "th", "col", "colgroup")
            // 체크리스트
            .allowAttributes("type").matching(INPUT_TYPE_CHECKBOX_ONLY).onElements("input")
            .allowAttributes("checked").onElements("input")
            // 에디터용 data-type (taskList, taskItem 등)
            .allowAttributes("data-type").onElements("ul", "ol", "li", "label", "div", "span", "p", "section", "article")
            // 공통 속성 (에디터 클래스/스타일 호환)
            .allowAttributes("class", "id", "title").globally()
            // style 허용 (OWASP 기본: 색상/폰트 등만, expression/behavior 등 차단)
            .allowStyling()
            // 속성 없이도 태그 유지 (span, input 등)
            .allowWithoutAttributes("span", "input", "a", "img")
            .toFactory();

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
