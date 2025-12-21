package com.sejong.projectservice.client;

import com.sejong.projectservice.domains.csknowledge.service.CsKnowledgeService;
import com.sejong.projectservice.domains.news.service.NewsService;
import com.sejong.projectservice.client.response.PostLikeCheckResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/archive")
public class NewsInternalController {

    private final NewsService newsService;
    private final CsKnowledgeService csKnowledgeService;

    @GetMapping("/check/news/{newsId}")
    @Operation(summary = "뉴스 존재 검증")
    public ResponseEntity<PostLikeCheckResponse> checkNewsId(@PathVariable("newsId") Long newsId) {
        PostLikeCheckResponse response = newsService.checkNews(newsId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check/cs/{csKnowledgeId}")
    @Operation(summary = "cs 지식 존재 검증")
    public ResponseEntity<PostLikeCheckResponse> checkCSKnowledgeId(@PathVariable("csKnowledgeId") Long csKnowledgeId) {
        PostLikeCheckResponse response = csKnowledgeService.checkCS(csKnowledgeId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/news/count")
    @Operation(summary = "Internal news 갯수 조회")
    public ResponseEntity<Long> getNewsCount() {
        Long count = newsService.getNewsCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/cs/count")
    @Operation(summary = "Internal CS 갯수 조회")
    public ResponseEntity<Long> getCsCount() {
        Long count = csKnowledgeService.getCsCount();
        return ResponseEntity.ok(count);
    }
}
