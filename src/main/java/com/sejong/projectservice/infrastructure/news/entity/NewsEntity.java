package com.sejong.archiveservice.infrastructure.news.entity;

import com.sejong.archiveservice.infrastructure.news.embeddable.ContentEmbeddable;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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
}
