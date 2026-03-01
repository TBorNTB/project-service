package com.sejong.projectservice.domains.qna.service;

import com.sejong.projectservice.domains.category.domain.CategoryEntity;
import com.sejong.projectservice.domains.category.repository.CategoryRepository;
import com.sejong.projectservice.domains.qna.domain.QuestionEntity;
import com.sejong.projectservice.domains.qna.dto.response.QuestionListResponse;
import com.sejong.projectservice.domains.qna.dto.response.QuestionResponse;
import com.sejong.projectservice.domains.qna.enums.QuestionListStatusFilter;
import com.sejong.projectservice.domains.qna.repository.QuestionAnswerRepository;
import com.sejong.projectservice.domains.qna.repository.QuestionRepository;
import com.sejong.projectservice.domains.qna.repository.spec.QuestionSpecifications;
import com.sejong.projectservice.support.common.exception.BaseException;
import com.sejong.projectservice.support.common.exception.ExceptionType;
import com.sejong.projectservice.support.common.internal.UserExternalService;
import com.sejong.projectservice.support.common.sanitizer.RequestSanitizer;
import com.sejong.projectservice.support.common.sanitizer.SanitizedQuestionInput;
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

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final RequestSanitizer requestSanitizer;
    private final QuestionRepository questionRepository;
    private final QuestionAnswerRepository questionAnswerRepository;
    private final UserExternalService userExternalService;
    private final CategoryRepository categoryRepository;

    @Transactional
    public QuestionResponse createQuestion(String title, String description, String content, List<String> categories, String username) {
        userExternalService.validateExistence(username);
        SanitizedQuestionInput sanitized = requestSanitizer.sanitizeQuestionInput(title, description, content, categories);
        List<CategoryEntity> categoryEntities = resolveExistingCategories(sanitized.categories());
        QuestionEntity question = QuestionEntity.of(sanitized.title(), sanitized.description(), sanitized.content(), username, categoryEntities);
        QuestionEntity saved = questionRepository.save(question);
        Map<String, UserNameInfo> userNameInfos = userExternalService.getUserNameInfos(List.of(saved.getUsername()));
        return QuestionResponse.from(saved, userNameInfos);
    }

        @Transactional(readOnly = true)
        public QuestionResponse findById(Long questionId) {
        QuestionEntity questionEntity = questionRepository.findWithCategoriesById(questionId)
            .orElseThrow(() -> new BaseException(ExceptionType.QUESTION_NOT_FOUND));

        Map<String, UserNameInfo> userNameInfos = userExternalService.getUserNameInfos(List.of(questionEntity.getUsername()));
        return QuestionResponse.from(questionEntity, userNameInfos);
        }

        @Transactional
        public QuestionResponse updateQuestion(
            Long questionId,
            String title,
            String description,
            String content,
            List<String> categories,
            String username
        ) {
        userExternalService.validateExistence(username);

        QuestionEntity questionEntity = questionRepository.findWithCategoriesById(questionId)
            .orElseThrow(() -> new BaseException(ExceptionType.QUESTION_NOT_FOUND));
        questionEntity.validateOwnerPermission(username);

        SanitizedQuestionInput sanitized = requestSanitizer.sanitizeQuestionInput(title, description, content, categories);
        List<CategoryEntity> categoryEntities = categories == null ? null : resolveExistingCategories(sanitized.categories());
        questionEntity.update(sanitized.title(), sanitized.description(), sanitized.content(), categoryEntities);

        Map<String, UserNameInfo> userNameInfos = userExternalService.getUserNameInfos(List.of(questionEntity.getUsername()));
        return QuestionResponse.from(questionEntity, userNameInfos);
        }

        @Transactional
        public void deleteQuestion(Long questionId, String username) {
        userExternalService.validateExistence(username);

        QuestionEntity questionEntity = questionRepository.findById(questionId)
            .orElseThrow(() -> new BaseException(ExceptionType.QUESTION_NOT_FOUND));
        questionEntity.validateOwnerPermission(username);

        questionRepository.deleteById(questionEntity.getId());
        }

        @Transactional(readOnly = true)
        public OffsetPageResponse<List<QuestionResponse>> getOffsetQuestions(OffsetPageReqDto offsetPageReqDto) {
        CustomPageRequest pageRequest = offsetPageReqDto.toPageRequest();
        Pageable pageable = PageRequest.of(
            pageRequest.getPage(),
            pageRequest.getSize(),
            Sort.Direction.valueOf(pageRequest.getDirection().name()),
            pageRequest.getSortBy()
        );

        Page<QuestionEntity> page = questionRepository.findAll(pageable);

        List<String> usernames = page.getContent().stream()
            .map(QuestionEntity::getUsername)
            .distinct()
            .toList();
        Map<String, UserNameInfo> userNameInfos = userExternalService.getUserNameInfos(usernames);

        List<QuestionResponse> responses = page.getContent().stream()
            .map(question -> QuestionResponse.from(question, userNameInfos))
            .toList();

        return OffsetPageResponse.ok(page.getNumber(), page.getTotalPages(), responses);
        }

    @Transactional(readOnly = true)
    public OffsetPageResponse<List<QuestionListResponse>> searchOffsetQuestions(
        OffsetPageReqDto offsetPageReqDto,
        QuestionListStatusFilter status,
        List<String> categoryNames,
        String keyword
    ) {
        CustomPageRequest pageRequest = offsetPageReqDto.toPageRequest();
        Pageable pageable = PageRequest.of(
            pageRequest.getPage(),
            pageRequest.getSize(),
            Sort.Direction.valueOf(pageRequest.getDirection().name()),
            pageRequest.getSortBy()
        );

        Page<QuestionEntity> page = questionRepository.findAll(
            QuestionSpecifications.filter(status, categoryNames, keyword),
            pageable
        );

        List<String> usernames = page.getContent().stream()
            .map(QuestionEntity::getUsername)
            .distinct()
            .toList();
        Map<String, UserNameInfo> userNameInfos = userExternalService.getUserNameInfos(usernames);

        List<Long> questionIds = page.getContent().stream()
            .map(QuestionEntity::getId)
            .toList();

        Map<Long, Long> answerCountMap = new HashMap<>();
        if (!questionIds.isEmpty()) {
            questionAnswerRepository.countByQuestionIds(questionIds)
                .forEach(p -> answerCountMap.put(p.getQuestionId(), p.getCnt()));
        }

        List<QuestionListResponse> responses = page.getContent().stream()
            .map(q -> QuestionListResponse.from(q, userNameInfos, answerCountMap.getOrDefault(q.getId(), 0L)))
            .toList();

        return OffsetPageResponse.ok(page.getNumber(), page.getTotalPages(), responses);
    }

    private List<CategoryEntity> resolveExistingCategories(List<String> categoryNames) {
        if (categoryNames == null || categoryNames.isEmpty()) {
            return List.of();
        }
        List<String> uniqueNames = categoryNames.stream()
            .filter(name -> name != null && !name.isBlank())
            .distinct()
            .toList();

        if (uniqueNames.isEmpty()) {
            return List.of();
        }

        return categoryRepository.findAllByNameIn(uniqueNames);

    }

    @Transactional(readOnly = true)
    public PostLikeCheckResponse checkQuestion(Long questionId) {
        boolean exists = questionRepository.existsById(questionId);
        if (exists) {
            QuestionEntity questionEntity = questionRepository.findById(questionId)
                    .orElseThrow(() -> new BaseException(ExceptionType.QUESTION_NOT_FOUND));
            return PostLikeCheckResponse.hasOfQuestion(questionEntity, true);
        }
        return PostLikeCheckResponse.hasNotOf();
    }
}
