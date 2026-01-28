package com.sejong.projectservice.domains.qna.controller;

import com.sejong.projectservice.domains.qna.dto.request.QuestionCreateRequest;
import com.sejong.projectservice.domains.qna.dto.request.QuestionUpdateRequest;
import com.sejong.projectservice.domains.qna.dto.response.QuestionListResponse;
import com.sejong.projectservice.domains.qna.dto.response.QuestionResponse;
import com.sejong.projectservice.domains.qna.enums.QuestionListStatusFilter;
import com.sejong.projectservice.domains.qna.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sejong.projectservice.support.common.pagination.OffsetPageReqDto;
import com.sejong.projectservice.support.common.pagination.OffsetPageResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/question")
public class QuestionController {

    private final QuestionService questionService;

    @Operation(summary = "질문글 생성")
    @PostMapping("")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<QuestionResponse> makeQuestion(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") String username,
            @Valid @RequestBody QuestionCreateRequest request
    ) {
        QuestionResponse response = questionService.createQuestion(request.getTitle(),request.getDescription(),request.getContent(),request.getCategories(),username);
        return ResponseEntity.status(201)
                .body(response);
    }

    @Operation(summary = "질문글 단건 조회")
    @GetMapping("/{questionId}")
    public ResponseEntity<QuestionResponse> getQuestion(@PathVariable Long questionId) {
        return ResponseEntity.ok(questionService.findById(questionId));
    }

    @Operation(summary = "질문글 수정")
    @PutMapping("/{questionId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<QuestionResponse> updateQuestion(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") String username,
            @PathVariable Long questionId,
            @Valid @RequestBody QuestionUpdateRequest request
    ) {
        QuestionResponse response = questionService.updateQuestion(
                questionId,
                request.getTitle(),
                request.getDescription(),
                request.getContent(),
                request.getCategories(),
                username
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "질문글 삭제")
    @DeleteMapping("/{questionId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteQuestion(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") String username,
            @PathVariable Long questionId
    ) {
        questionService.deleteQuestion(questionId, username);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "질문글 목록 조회 (오프셋 기반 페이지네이션)")
    @GetMapping("/offset")
    public ResponseEntity<OffsetPageResponse<List<QuestionResponse>>> getOffsetQuestions(
            @ParameterObject @Valid OffsetPageReqDto offsetPageReqDto
    ) {
        return ResponseEntity.ok(questionService.getOffsetQuestions(offsetPageReqDto));
    }

    @Operation(summary = "질문글 목록 검색/필터 조회 (상태/기술태그/키워드)")
    @GetMapping("/offset/search")
    public ResponseEntity<OffsetPageResponse<List<QuestionListResponse>>> searchOffsetQuestions(
        @ParameterObject @Valid OffsetPageReqDto offsetPageReqDto,
        @RequestParam(name = "status", required = false, defaultValue = "ALL") QuestionListStatusFilter status,
        @RequestParam(name = "categoryNames", required = false) List<String> categoryNames,
        @RequestParam(name = "keyword", required = false) String keyword
    ) {
        return ResponseEntity.ok(questionService.searchOffsetQuestions(offsetPageReqDto, status, categoryNames, keyword));
    }
}
