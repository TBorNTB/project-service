package com.sejong.projectservice.domains.news.domain;


import com.sejong.projectservice.support.common.exception.BaseException;
import com.sejong.projectservice.support.common.exception.ExceptionType;
import com.sejong.projectservice.support.common.file.Filepath;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    @Column(name = "thumbnail_path")
    private String thumbnailPath;

    @Column(name = "writer_id", nullable = false)
    private String writerId;

    @Column(name = "news_user_ids")
    private String participantIds;

    @Column(name = "tag")
    private String tags;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    private NewsEntity(Long id, ContentEmbeddable content, String thumbnailPath,
                       String writerId, String participantIds, String tags,
                       LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.content = content;
        this.thumbnailPath = thumbnailPath;
        this.writerId = writerId;
        this.participantIds = participantIds;
        this.tags = tags;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void update(ContentEmbeddable content, String participantIds, String tags) {
        this.content = content;
        this.participantIds = participantIds;
        this.tags = tags;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateFileInfo(Filepath filepath) {
        this.thumbnailPath =  filepath.path();
    }

    public void validateOwner(String writerId) {
        if (!this.writerId.equals(writerId)) {
            throw new BaseException(ExceptionType.NOT_NEWS_OWNER);
        }
    }
}
