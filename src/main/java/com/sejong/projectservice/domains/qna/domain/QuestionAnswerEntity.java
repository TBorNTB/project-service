package com.sejong.projectservice.domains.qna.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sejong.projectservice.support.common.exception.BaseException;
import com.sejong.projectservice.support.common.exception.ExceptionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "question-answer")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class QuestionAnswerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "username", nullable = false)
    private String username;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Column(name = "accepted")
    private Boolean accepted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionEntity questionEntity;

    public void assignQuestionEntity(QuestionEntity questionEntity) {
        this.questionEntity = questionEntity;
    }

    public static QuestionAnswerEntity of(String content, String username, QuestionEntity questionEntity) {
        LocalDateTime now = LocalDateTime.now();
        return QuestionAnswerEntity.builder()
                .id(null)
                .content(content)
                .username(username)
                .createdAt(now)
                .updatedAt(now)
                .questionEntity(questionEntity)
                .accepted(false)
                .build();
    }

    public void update(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    public void validateOwnerPermission(String username) {
        if (username == null || !username.equals(this.username)) {
            throw new BaseException(ExceptionType.FORBIDDEN);
        }
    }

    public void markNotAccepted() {
        accepted = false;
        updatedAt = LocalDateTime.now();
    }

    public void markAccepted() {
        accepted = true;
        updatedAt = LocalDateTime.now();
    }
}
