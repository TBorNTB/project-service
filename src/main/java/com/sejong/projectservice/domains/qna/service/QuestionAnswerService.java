package com.sejong.projectservice.domains.qna.service;

import com.sejong.projectservice.domains.qna.domain.QuestionAnswerEntity;
import com.sejong.projectservice.domains.qna.domain.QuestionEntity;
import com.sejong.projectservice.domains.qna.dto.response.QuestionAnswerResponse;
import com.sejong.projectservice.domains.qna.repository.QuestionAnswerRepository;
import com.sejong.projectservice.domains.qna.repository.QuestionRepository;
import com.sejong.projectservice.support.common.exception.BaseException;
import com.sejong.projectservice.support.common.exception.ExceptionType;
import com.sejong.projectservice.support.common.internal.UserExternalService;
import com.sejong.projectservice.support.common.internal.response.PostLikeCheckResponse;
import com.sejong.projectservice.support.common.internal.response.UserNameInfo;
import com.sejong.projectservice.support.common.pagination.CustomPageRequest;
import com.sejong.projectservice.support.common.pagination.OffsetPageReqDto;
import com.sejong.projectservice.support.common.pagination.OffsetPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QuestionAnswerService {

    private final QuestionAnswerRepository questionAnswerRepository;
    private final QuestionRepository questionRepository;
    private final UserExternalService userExternalService;

    @Transactional
    public QuestionAnswerResponse createAnswer(Long questionId, String content, String username) {
        userExternalService.validateExistence(username);

        QuestionEntity questionEntity = questionRepository.findById(questionId)
                .orElseThrow(() -> new BaseException(ExceptionType.QUESTION_NOT_FOUND));

        QuestionAnswerEntity answerEntity = QuestionAnswerEntity.of(content, username, questionEntity);
        QuestionAnswerEntity saved = questionAnswerRepository.save(answerEntity);
        Map<String, UserNameInfo> userNameInfos = userExternalService.getUserNameInfos(List.of(saved.getUsername()));
        return QuestionAnswerResponse.from(saved, userNameInfos);
    }

    @Transactional(readOnly = true)
    public QuestionAnswerResponse findById(Long answerId) {
        QuestionAnswerEntity entity = questionAnswerRepository.findById(answerId)
                .orElseThrow(() -> new BaseException(ExceptionType.QUESTION_ANSWER_NOT_FOUND));
        Map<String, UserNameInfo> userNameInfos = userExternalService.getUserNameInfos(List.of(entity.getUsername()));
        return QuestionAnswerResponse.from(entity, userNameInfos);
    }

    @Transactional
    public QuestionAnswerResponse updateAnswer(Long answerId, String content, String username) {
        userExternalService.validateExistence(username);

        QuestionAnswerEntity entity = questionAnswerRepository.findById(answerId)
                .orElseThrow(() -> new BaseException(ExceptionType.QUESTION_ANSWER_NOT_FOUND));
        entity.validateOwnerPermission(username);

        entity.update(content);
        Map<String, UserNameInfo> userNameInfos = userExternalService.getUserNameInfos(List.of(entity.getUsername()));
        return QuestionAnswerResponse.from(entity, userNameInfos);
    }

    @Transactional
    public void deleteAnswer(Long answerId, String username) {
        userExternalService.validateExistence(username);

        QuestionAnswerEntity entity = questionAnswerRepository.findById(answerId)
                .orElseThrow(() -> new BaseException(ExceptionType.QUESTION_ANSWER_NOT_FOUND));
        entity.validateOwnerPermission(username);

        questionAnswerRepository.deleteById(entity.getId());
    }

    @Transactional(readOnly = true)
    public OffsetPageResponse<List<QuestionAnswerResponse>> getOffsetAnswers(Long questionId, OffsetPageReqDto offsetPageReqDto) {
        CustomPageRequest pageRequest = offsetPageReqDto.toPageRequest();
        Pageable pageable = PageRequest.of(
                pageRequest.getPage(),
                pageRequest.getSize(),
                Sort.Direction.valueOf(pageRequest.getDirection().name()),
                pageRequest.getSortBy()
        );

        Page<QuestionAnswerEntity> page = questionAnswerRepository.findAllByQuestionEntityId(questionId, pageable);

        List<String> usernames = page.getContent().stream()
            .map(QuestionAnswerEntity::getUsername)
            .distinct()
            .toList();
        Map<String, UserNameInfo> userNameInfos = userExternalService.getUserNameInfos(usernames);

        List<QuestionAnswerResponse> responses = page.getContent().stream()
            .map(answer -> QuestionAnswerResponse.from(answer, userNameInfos))
            .toList();

        return OffsetPageResponse.ok(page.getNumber(), page.getTotalPages(), responses);
    }

    @Transactional
    public void acceptAnswerToggle(Long answerId, String username) {
        userExternalService.validateExistence(username);

        QuestionAnswerEntity selectedAnswer = questionAnswerRepository.findById(answerId)
                .orElseThrow(() -> new BaseException(ExceptionType.QUESTION_ANSWER_NOT_FOUND));

        QuestionEntity questionEntity = selectedAnswer.getQuestionEntity();
        questionEntity.validateOwnerPermission(username);

        if (selectedAnswer.getAccepted()) {
            selectedAnswer.markNotAccepted();

            boolean hasOtherAcceptedAnswers = questionAnswerRepository
                    .existsByQuestionEntityIdAndAcceptedTrueAndIdNot(questionEntity.getId(), selectedAnswer.getId());
            if (!hasOtherAcceptedAnswers) {
                questionEntity.markNotAccepted();
            }
            return;
        }

        selectedAnswer.markAccepted();
        questionEntity.markAccepted();
        // Dirty checking 으로 자동 저장
    }

    @Transactional(readOnly = true)
    public PostLikeCheckResponse checkAnswer(Long answerId) {
        boolean exists = questionAnswerRepository.existsById(answerId);
        if (exists) {
            QuestionAnswerEntity answerEntity = questionAnswerRepository.findById(answerId)
                    .orElseThrow(() -> new BaseException(ExceptionType.QUESTION_ANSWER_NOT_FOUND));
            return PostLikeCheckResponse.hasOfAnswer(answerEntity, true);
        }
        return PostLikeCheckResponse.hasNotOf();
    }
}
