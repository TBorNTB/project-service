package com.sejong.projectservice.support.common.internal;

import com.sejong.projectservice.domains.qna.service.QuestionAnswerService;
import com.sejong.projectservice.domains.qna.service.QuestionService;
import com.sejong.projectservice.support.common.internal.response.PostLikeCheckResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/qna")
public class QnaInternalController {

    private final QuestionService questionService;
    private final QuestionAnswerService questionAnswerService;

    @GetMapping("/check/question/{questionId}")
    @Operation(summary = "질문 존재 검증")
    public ResponseEntity<PostLikeCheckResponse> checkQnaQuestion(@PathVariable("questionId") Long questionId) {
        PostLikeCheckResponse response = questionService.checkQuestion(questionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check/answer/{answerId}")
    @Operation(summary = "답변 존재 검증")
    public ResponseEntity<PostLikeCheckResponse> checkQnaAnswer(@PathVariable("answerId") Long answerId) {
        PostLikeCheckResponse response = questionAnswerService.checkAnswer(answerId);
        return ResponseEntity.ok(response);
    }
}
