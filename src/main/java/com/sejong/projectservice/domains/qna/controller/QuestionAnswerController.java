package com.sejong.projectservice.domains.qna.controller;

import com.sejong.projectservice.domains.qna.dto.request.QuestionAnswerCreateRequest;
import com.sejong.projectservice.domains.qna.dto.request.QuestionAnswerUpdateRequest;
import com.sejong.projectservice.domains.qna.dto.response.QuestionAnswerResponse;
import com.sejong.projectservice.domains.qna.service.QuestionAnswerService;
import com.sejong.projectservice.support.common.pagination.OffsetPageReqDto;
import com.sejong.projectservice.support.common.pagination.OffsetPageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/question")
public class QuestionAnswerController {

    private final QuestionAnswerService questionAnswerService;

    @Operation(summary = "질문 답변 생성")
    @PostMapping("/{questionId}/answer")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<QuestionAnswerResponse> createAnswer(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") String username,
            @PathVariable Long questionId,
            @Valid @RequestBody QuestionAnswerCreateRequest request
    ) {
        QuestionAnswerResponse response = questionAnswerService.createAnswer(questionId, request.getContent(), username);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(summary = "질문 답변 단건 조회")
    @GetMapping("/answer/{answerId}")
    public ResponseEntity<QuestionAnswerResponse> getAnswer(@PathVariable Long answerId) {
        return ResponseEntity.ok(questionAnswerService.findById(answerId));
    }

    @Operation(summary = "질문 답변 수정")
    @PutMapping("/answer/{answerId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<QuestionAnswerResponse> updateAnswer(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") String username,
            @PathVariable Long answerId,
            @Valid @RequestBody QuestionAnswerUpdateRequest request
    ) {
        return ResponseEntity.ok(questionAnswerService.updateAnswer(answerId, request.getContent(), username));
    }

    @Operation(summary = "질문 답변 삭제")
    @DeleteMapping("/answer/{answerId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteAnswer(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") String username,
            @PathVariable Long answerId
    ) {
        questionAnswerService.deleteAnswer(answerId, username);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "질문 답변 목록 조회 (오프셋 기반 페이지네이션)")
    @GetMapping("/{questionId}/answer/offset")
    public ResponseEntity<OffsetPageResponse<List<QuestionAnswerResponse>>> getOffsetAnswers(
            @PathVariable Long questionId,
            @ParameterObject @Valid OffsetPageReqDto offsetPageReqDto
    ) {
        return ResponseEntity.ok(questionAnswerService.getOffsetAnswers(questionId, offsetPageReqDto));
    }

    @Operation(summary = "질문 답변 채택")
    @PostMapping("/{questionId}/answer/{answerId}/accept")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> acceptAnswerToggle(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") String username,
            @PathVariable Long answerId
    ) {
        questionAnswerService.acceptAnswerToggle(answerId, username);
        return ResponseEntity.noContent().build();
    }
}
