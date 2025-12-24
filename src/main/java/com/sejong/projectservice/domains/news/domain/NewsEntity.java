package com.sejong.projectservice.domains.news.domain;


import com.sejong.projectservice.domains.news.dto.NewsReqDto;
import com.sejong.projectservice.domains.news.dto.NewsResDto;
import com.sejong.projectservice.domains.user.UserId;
import com.sejong.projectservice.domains.user.UserIds;
import com.sejong.projectservice.support.common.constants.NewsCategory;
import com.sejong.projectservice.support.common.exception.BaseException;
import com.sejong.projectservice.support.common.exception.ExceptionType;
import com.sejong.projectservice.support.common.file.Filepath;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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

    public static NewsEntity from(NewsReqDto newsReqDto, LocalDateTime time) {
        Content content = Content.of(newsReqDto.getTitle(), newsReqDto.getSummary(), newsReqDto.getContent(),
                NewsCategory.of(newsReqDto.getCategory()));
        UserId userId = UserId.of(newsReqDto.getWriterUsername());
        UserIds userIds = UserIds.of(newsReqDto.getParticipantIds());

        ContentEmbeddable contentEmbeddable = ContentEmbeddable.of(content);

        return NewsEntity.builder()
                .id(null)
                .content(contentEmbeddable)
                .thumbnailPath(null)
                .writerId(userId.userId())
                .participantIds(userIds.toString())
                .tags(newsReqDto.getTags().toString())
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

    public UserIds toParticipantIdsVo() {
        return UserIds.of(participantIds);
    }

    public List<String> toTagsList(){
        return Arrays.stream(this.getTags().split(",")).toList();
    }

    public Filepath toFilepathVo(){
        return Filepath.of(thumbnailPath);
    }

    public void update(NewsReqDto newsReqDto, String participantIds, String tags) {

        Content content = Content.of(newsReqDto.getTitle(), newsReqDto.getSummary(), newsReqDto.getContent(),
                NewsCategory.of(newsReqDto.getCategory()));

        this.content = ContentEmbeddable.of(content);
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
