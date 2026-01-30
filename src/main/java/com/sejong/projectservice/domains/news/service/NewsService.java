package com.sejong.projectservice.domains.news.service;


import com.sejong.projectservice.domains.news.domain.NewsEntity;
import com.sejong.projectservice.domains.news.dto.NewsReqDto;
import com.sejong.projectservice.domains.news.dto.NewsResDto;
import com.sejong.projectservice.domains.news.kafka.dto.NewsCreatedEventDto;
import com.sejong.projectservice.domains.news.kafka.dto.NewsDeletedEventDto;
import com.sejong.projectservice.domains.news.kafka.dto.NewsUpdatedEventDto;
import com.sejong.projectservice.domains.news.repository.ArchiveRepository;
import com.sejong.projectservice.domains.user.UserIds;
import com.sejong.projectservice.support.common.exception.BaseException;
import com.sejong.projectservice.support.common.exception.ExceptionType;
import com.sejong.projectservice.support.common.file.FileUploader;
import com.sejong.projectservice.support.common.internal.UserExternalService;
import com.sejong.projectservice.support.common.internal.response.PostLikeCheckResponse;
import com.sejong.projectservice.support.common.internal.response.UserNameInfo;
import com.sejong.projectservice.support.common.pagination.CursorPageReqDto;
import com.sejong.projectservice.support.common.pagination.CursorPageRequest;
import com.sejong.projectservice.support.common.pagination.CursorPageRes;
import com.sejong.projectservice.support.common.pagination.CustomPageRequest;
import com.sejong.projectservice.support.common.pagination.OffsetPageReqDto;
import com.sejong.projectservice.support.common.pagination.OffsetPageResponse;
import com.sejong.projectservice.support.common.pagination.enums.SortDirection;
import com.sejong.projectservice.support.common.util.ExtractorUsername;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class NewsService {

    private final ArchiveRepository archiveRepository;
    private final UserExternalService userExternalService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final FileUploader fileUploader;

    @Transactional
    public NewsResDto createNews(NewsReqDto newsReqDto) {
        userExternalService.validateExistence(
                newsReqDto.getWriterUsername(),
                newsReqDto.getParticipantIds()
        );

        NewsEntity newsEntity = NewsEntity.of(
                newsReqDto.getTitle(),
                newsReqDto.getSummary(),
                newsReqDto.getContent(),
                newsReqDto.getCategory(),
                newsReqDto.getWriterUsername(),
                newsReqDto.getParticipantIds(),
                newsReqDto.getTags(),
                LocalDateTime.now()
        );
        NewsEntity savedNewsEntity = archiveRepository.save(newsEntity);

        // 썸네일 파일 처리 (temp → 최종 위치)
        if (newsReqDto.getThumbnailKey() != null && !newsReqDto.getThumbnailKey().isEmpty()) {
            String targetDir = String.format("project-service/news/%d/thumbnail", savedNewsEntity.getId());
            String finalKey = fileUploader.moveFile(newsReqDto.getThumbnailKey(), targetDir);
            savedNewsEntity.updateThumbnailKey(finalKey);
        }

        // 에디터 본문 이미지 처리 (temp → 최종 위치) 및 content URL 치환
        if (newsReqDto.getContentImageKeys() != null && !newsReqDto.getContentImageKeys().isEmpty()) {
            String updatedContent = processContentImages(
                    savedNewsEntity.getId(),
                    newsReqDto.getContent(),
                    newsReqDto.getContentImageKeys()
            );
            savedNewsEntity.updateContent(updatedContent);
        }

        applicationEventPublisher.publishEvent(NewsCreatedEventDto.of(savedNewsEntity.getId()));
        return resolveUsernames(savedNewsEntity);
    }

    @Transactional
    public NewsResDto updateNews(Long newsId, NewsReqDto newsReqDto, String writerId) {
        NewsEntity newsEntity = archiveRepository.findById(newsId)
                .orElseThrow(() -> new BaseException(ExceptionType.NEWS_NOT_FOUND));
        newsEntity.validateOwner(writerId);

        newsEntity.update(
                newsReqDto.getTitle(),
                newsReqDto.getSummary(),
                newsReqDto.getContent(),
                newsReqDto.getCategory(),
                UserIds.of(newsReqDto.getParticipantIds()).toString(),
                String.join(",", newsReqDto.getTags())
        );

        // 새 썸네일이 전달된 경우 (temp key)
        if (newsReqDto.getThumbnailKey() != null && !newsReqDto.getThumbnailKey().isEmpty()) {
            // 기존 썸네일 삭제
            if (newsEntity.getThumbnailKey() != null) {
                try {
                    fileUploader.delete(newsEntity.getThumbnailKey());
                } catch (Exception e) {
                    log.warn("기존 썸네일 삭제 실패, 계속 진행: {}", newsEntity.getThumbnailKey(), e);
                }
            }
            // 새 썸네일 이동
            String targetDir = String.format("project-service/news/%d/thumbnail", newsEntity.getId());
            String finalKey = fileUploader.moveFile(newsReqDto.getThumbnailKey(), targetDir);
            newsEntity.updateThumbnailKey(finalKey);
        }

        // 새 에디터 이미지가 전달된 경우
        if (newsReqDto.getContentImageKeys() != null && !newsReqDto.getContentImageKeys().isEmpty()) {
            String updatedContent = processContentImages(
                    newsEntity.getId(),
                    newsEntity.toContentVo().getContent(),
                    newsReqDto.getContentImageKeys()
            );
            newsEntity.updateContent(updatedContent);
        }

        applicationEventPublisher.publishEvent(NewsUpdatedEventDto.of(newsEntity.getId()));

        return resolveUsernames(newsEntity);
    }

    @Transactional
    public void deleteNews(Long newsId, String writerId) {
        NewsEntity newsEntity = archiveRepository.findById(newsId)
                .orElseThrow(() -> new BaseException(ExceptionType.NEWS_NOT_FOUND));
        newsEntity.validateOwner(writerId);

        archiveRepository.deleteById(newsEntity.getId());
        applicationEventPublisher.publishEvent(NewsDeletedEventDto.of(newsEntity.getId()));
    }

    public NewsResDto findById(Long newsId) {
        NewsEntity newsEntity = archiveRepository.findById(newsId)
                .orElseThrow(() -> new BaseException(ExceptionType.NEWS_NOT_FOUND));

        return resolveUsernames(newsEntity);
    }

    @Transactional(readOnly = true)
    public OffsetPageResponse<List<NewsResDto>> getOffsetNews(OffsetPageReqDto offsetPageReqDto) {
        CustomPageRequest pageRequest = offsetPageReqDto.toPageRequest();

        Pageable pageable = PageRequest.of(pageRequest.getPage(),
                pageRequest.getSize(),
                Sort.Direction.valueOf(pageRequest.getDirection().name()),
                pageRequest.getSortBy());

        Page<NewsEntity> archiveEntities = archiveRepository.findAll(pageable);

        List<NewsResDto> dtoList = archiveEntities.getContent().stream()
                .map(this::resolveUsernames)
                .toList();

        return OffsetPageResponse.ok(archiveEntities.getNumber(), archiveEntities.getTotalPages(), dtoList);
    }

    @Transactional(readOnly = true)
    public CursorPageRes<List<NewsResDto>> getCursorNews(CursorPageReqDto cursorPageReqDto) {
        CursorPageRequest pageRequest = cursorPageReqDto.toPageRequest();

        Pageable pageable = PageRequest.of(0, pageRequest.getSize() + 1);
        List<NewsEntity> entities = getCursorBasedEntities(pageRequest, pageable);

        List<NewsResDto> dtoList = entities.stream()
                .map(this::resolveUsernames)
                .toList();

        return CursorPageRes.from(
                dtoList,
                pageRequest.getSize(),
                NewsResDto::id
        );
    }

    @Transactional(readOnly = true)
    public PostLikeCheckResponse checkNews(Long newsId) {
        boolean exists = archiveRepository.existsById(newsId);
        if (exists) {
            NewsEntity newsEntity = archiveRepository.findById(newsId)
                    .orElseThrow(() -> new BaseException(ExceptionType.NEWS_NOT_FOUND));
            return PostLikeCheckResponse.hasOfNews(newsEntity, true);
        }
        return PostLikeCheckResponse.hasNotOf();
    }

    private NewsResDto resolveUsernames(NewsEntity newsEntity) {
        List<String> usernames = ExtractorUsername.FromNewses(newsEntity);
        Map<String, UserNameInfo> usernamesMap = userExternalService.getUserNameInfos(usernames);
        return NewsResDto.from(newsEntity, usernamesMap, fileUploader);
    }

    /**
     * 에디터 본문 이미지를 temp에서 최종 위치로 이동하고 content 내 key 치환
     * content는 Tiptap JSON 형식이며, image 노드의 key 속성을 치환
     */
    private String processContentImages(Long newsId, String content, List<String> imageKeys) {
        String updatedContent = content;
        String targetDir = String.format("project-service/news/%d/images", newsId);

        for (String tempKey : imageKeys) {
            if (tempKey == null || tempKey.isEmpty()) continue;

            try {
                String finalKey = fileUploader.moveFile(tempKey, targetDir);
                // temp key → final key 치환 (URL이 아닌 key만 치환)
                updatedContent = updatedContent.replace(tempKey, finalKey);
            } catch (Exception e) {
                log.warn("이미지 이동 실패, 스킵: {}", tempKey, e);
            }
        }
        return updatedContent;
    }

    @Transactional(readOnly = true)
    public Long getNewsCount() {
        Long count = archiveRepository.getNewsCount();
        return count;
    }

    @Transactional(readOnly = true)
    public Long getNewsCountByDate(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
        return archiveRepository.getNewsCountByDate(startDateTime, endDateTime);
    }

    @Transactional(readOnly = true)
    public List<Long> getNewsIdsByUsername(String username) {
        return archiveRepository.findNewsIdsByUsername(username);
    }

    private List<NewsEntity> getCursorBasedEntities(CursorPageRequest request, Pageable pageable) {
        boolean isDesc = request.getDirection() == SortDirection.DESC;

        if (request.getCursor() == null) {
            // 첫 페이지
            return isDesc ?
                    archiveRepository.findFirstPageDesc(pageable) :
                    archiveRepository.findFirstPageAsc(pageable);
        } else {
            // 커서 기반 페이지
            return isDesc ?
                    archiveRepository.findByCursorDesc(request.getCursor().getProjectId(), pageable) :
                    archiveRepository.findByCursorAsc(request.getCursor().getProjectId(), pageable);
        }
    }
}