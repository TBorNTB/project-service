package com.sejong.archiveservice.application.news.service;

import com.sejong.archiveservice.application.internal.UserExternalService;
import com.sejong.archiveservice.application.internal.response.PostLikeCheckResponse;
import com.sejong.archiveservice.application.news.assembler.NewsAssembler;
import com.sejong.archiveservice.application.news.dto.NewsReqDto;
import com.sejong.archiveservice.application.news.dto.NewsResDto;
import com.sejong.archiveservice.application.pagination.CursorPageReqDto;
import com.sejong.archiveservice.application.pagination.OffsetPageReqDto;
import com.sejong.archiveservice.client.dto.UserNameInfo;
import com.sejong.archiveservice.core.common.extractor.ExtractorUsername;
import com.sejong.archiveservice.core.common.pagination.CursorPageRequest;
import com.sejong.archiveservice.core.common.pagination.CursorPageResponse;
import com.sejong.archiveservice.core.common.pagination.CustomPageRequest;
import com.sejong.archiveservice.core.common.pagination.OffsetPageResponse;
import com.sejong.archiveservice.core.news.News;
import com.sejong.archiveservice.core.news.NewsRepository;
import com.sejong.archiveservice.core.user.UserId;
import com.sejong.archiveservice.core.user.UserIds;
import com.sejong.archiveservice.infrastructure.news.kafka.NewsEventPublisher;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsService {

    private final NewsRepository newsRepository;
    private final UserExternalService userExternalService;
    private final NewsEventPublisher newsEventPublisher;

    @Transactional
    public NewsResDto createNews(NewsReqDto newsReqDto) {
        userExternalService.validateExistence(
                newsReqDto.getWriterUsername(),
                newsReqDto.getParticipantIds()
        );

        News news = NewsAssembler.toNews(newsReqDto);
        News savedNews = newsRepository.save(news);

        newsEventPublisher.publishCreated(savedNews);

        return resolveUsernames(savedNews);
    }

    @Transactional
    public NewsResDto updateNews(Long newsId, NewsReqDto newsReqDto, String writerId) {
        News news = newsRepository.findBy(newsId);
        news.validateOwner(UserId.of(writerId));

        news.update(
                NewsAssembler.toContent(newsReqDto),
                UserIds.of(newsReqDto.getParticipantIds()),
                newsReqDto.getTags()
        );

        News updatedNews = newsRepository.update(news);
        newsEventPublisher.publishUpdated(updatedNews);

        return resolveUsernames(updatedNews);
    }

    @Transactional
    public void deleteNews(Long newsId, String writerId) {
        News news = newsRepository.findBy(newsId);
        news.validateOwner(UserId.of(writerId));

        newsRepository.delete(news);
        newsEventPublisher.publishDeleted(newsId);
    }

    public NewsResDto findById(Long newsId) {
        News news = newsRepository.findBy(newsId);
        return resolveUsernames(news);
    }

    @Transactional(readOnly = true)
    public OffsetPageResponse<List<NewsResDto>> getOffsetNews(OffsetPageReqDto offsetPageReqDto) {
        CustomPageRequest pageRequest = offsetPageReqDto.toPageRequest();
        OffsetPageResponse<List<News>> newsPage = newsRepository.findAllWithOffset(pageRequest);

        List<NewsResDto> dtoList = newsPage.getData().stream()
                .map(this::resolveUsernames)
                .toList();

        return OffsetPageResponse.ok(newsPage.getPage(), newsPage.getTotalPage(), dtoList);
    }

    @Transactional(readOnly = true)
    public CursorPageResponse<List<NewsResDto>> getCursorNews(CursorPageReqDto cursorPageReqDto) {
        CursorPageRequest pageRequest = cursorPageReqDto.toPageRequest();
        CursorPageResponse<List<News>> newsPage = newsRepository.findAllWithCursor(pageRequest);

        List<NewsResDto> dtoList = newsPage.getData().stream()
                .map(this::resolveUsernames)
                .toList();

        return CursorPageResponse.ok(newsPage.getNextCursor(), newsPage.isHasNext(), dtoList);
    }

    @Transactional(readOnly = true)
    public PostLikeCheckResponse checkNews(Long newsId) {
        boolean exists = newsRepository.existsNews(newsId);
        if (exists) {
            News news = newsRepository.findBy(newsId);
            return PostLikeCheckResponse.hasOfNews(news, true);
        }
        return PostLikeCheckResponse.hasNotOf();
    }

    private NewsResDto resolveUsernames(News news) {
        List<String> usernames = ExtractorUsername.FromNewses(news);
        Map<String, UserNameInfo> usernamesMap = userExternalService.getAllUsernames(usernames);
        return NewsResDto.from(news, usernamesMap);
    }

    @Transactional(readOnly = true)
    public Long getNewsCount() {
        Long count = newsRepository.getNewsCount();
        return count;
    }
}