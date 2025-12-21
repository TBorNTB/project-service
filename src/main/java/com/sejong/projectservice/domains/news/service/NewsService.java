package com.sejong.projectservice.domains.news.service;


import com.sejong.projectservice.domains.news.util.NewsAssembler;
import com.sejong.projectservice.domains.news.dto.NewsReqDto;
import com.sejong.projectservice.domains.news.dto.NewsResDto;
import com.sejong.projectservice.support.common.pagination.CursorPageReqDto;
import com.sejong.projectservice.support.common.pagination.OffsetPageReqDto;
import com.sejong.projectservice.client.UserExternalService;
import com.sejong.projectservice.client.response.PostLikeCheckResponse;
import com.sejong.projectservice.client.response.UserNameInfo;
import com.sejong.projectservice.support.common.util.ExtractorUsername;
import com.sejong.projectservice.support.common.pagination.CursorPageRequest;
import com.sejong.projectservice.support.common.pagination.CursorPageResponse;
import com.sejong.projectservice.support.common.pagination.CustomPageRequest;
import com.sejong.projectservice.support.common.pagination.OffsetPageResponse;
import com.sejong.projectservice.domains.news.domain.News;
import com.sejong.projectservice.domains.news.repository.NewsRepository;
import com.sejong.projectservice.domains.user.UserId;
import com.sejong.projectservice.domains.user.UserIds;
import com.sejong.projectservice.domains.news.kafka.NewsEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
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

        List<NewsResDto> dtoList = newsPage.getContent().stream()
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
        log.info("usernames.size() {}",usernames.size());
        for(int i=0;i<usernames.size();i++){
            log.info("username.get({}) : {}",i,usernames.get(i));
        }
        Map<String, UserNameInfo> usernamesMap = userExternalService.getUserNameInfos(usernames);
        return NewsResDto.from(news, usernamesMap);
    }

    @Transactional(readOnly = true)
    public Long getNewsCount() {
        Long count = newsRepository.getNewsCount();
        return count;
    }
}