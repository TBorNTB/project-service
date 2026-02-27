package com.sejong.projectservice.domains.news.domain;


import com.sejong.projectservice.support.common.constants.NewsCategory;
import com.sejong.projectservice.support.common.exception.BaseException;
import com.sejong.projectservice.support.common.exception.ExceptionType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "news")
@Getter
@NoArgsConstructor
public class NewsEntity {

    @Id
    @Column(name = "news_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private ContentEmbeddable content;

    @Column(name = "thumbnail_key")
    private String thumbnailKey;

    @Column(name = "writer_id", nullable = false)
    private String writerUsername;

    @Column(name = "news_user_ids")
    private String participantUsernames;

    @Column(name = "tag")
    private String tags;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    private NewsEntity(Long id, ContentEmbeddable content, String thumbnailKey,
                       String writerUsername, String participantUsernames, String tags,
                       LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.content = content;
        this.thumbnailKey = thumbnailKey;
        this.writerUsername = writerUsername;
        this.participantUsernames = participantUsernames;
        this.tags = tags;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static NewsEntity of(String title, String summary, String content, String category, String writerUsername,
                                List<String> participantUsernames, List<String> tags, LocalDateTime time) {
        ContentEmbeddable contentEmbeddable = ContentEmbeddable.of(
                Content.of(title, summary, content, NewsCategory.of(category)));

        return NewsEntity.builder()
                .id(null)
                .content(contentEmbeddable)
                .thumbnailKey(null)
                .writerUsername(writerUsername)
                .participantUsernames(String.join(",", participantUsernames))
                .tags(String.join(",", tags))
                .createdAt(time)
                .updatedAt(time)
                .build();
    }

    public Content toContentVo() {
        return Content.of(
                content.getTitle(),
                content.getSummary(),
                content.getContent(),
                content.getCategory()
        );
    }

    public List<String> toParticipantUsernameList() {
        if (participantUsernames == null || participantUsernames.isBlank()) {
            return new java.util.ArrayList<>();
        }
        return Arrays.stream(participantUsernames.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
    }

    public List<String> toTagsList() {
        if (this.getTags() == null || this.getTags().isEmpty()) {
            return List.of();
        }
        return Arrays.stream(this.getTags().split(",")).toList();
    }

    public void update(String title, String summary, String content, String category, String participantIds,
                       String tags) {
        Content contentVo = Content.of(title, summary, content, NewsCategory.of(category));
        this.content = ContentEmbeddable.of(contentVo);
        this.participantUsernames = participantIds;
        this.tags = tags;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateThumbnailKey(String thumbnailKey) {
        this.thumbnailKey = thumbnailKey;
    }

    public void updateContent(String newContent) {
        this.content = ContentEmbeddable.of(
                Content.of(
                        this.content.getTitle(),
                        this.content.getSummary(),
                        newContent,
                        this.content.getCategory()
                )
        );
        this.updatedAt = LocalDateTime.now();
    }

    public void validateOwner(String writerId) {
        if (!this.writerUsername.equals(writerId)) {
            throw new BaseException(ExceptionType.NOT_NEWS_OWNER);
        }
    }
}
