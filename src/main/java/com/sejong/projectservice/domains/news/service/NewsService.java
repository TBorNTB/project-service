package com.sejong.projectservice.domains.news.service;


import com.sejong.projectservice.domains.news.domain.ContentEmbeddable;
import com.sejong.projectservice.domains.news.domain.NewsEntity;
import com.sejong.projectservice.domains.news.repository.ArchiveRepository;
import com.sejong.projectservice.domains.news.util.NewsAssembler;
import com.sejong.projectservice.domains.news.dto.NewsReqDto;
import com.sejong.projectservice.domains.news.dto.NewsResDto;
import com.sejong.projectservice.domains.news.util.NewsMapper;
import com.sejong.projectservice.support.common.exception.BaseException;
import com.sejong.projectservice.support.common.exception.ExceptionType;
import com.sejong.projectservice.support.common.pagination.CursorPageReqDto;
import com.sejong.projectservice.support.common.pagination.OffsetPageReqDto;
import com.sejong.projectservice.client.UserExternalService;
import com.sejong.projectservice.client.response.PostLikeCheckResponse;
import com.sejong.projectservice.client.response.UserNameInfo;
import com.sejong.projectservice.support.common.pagination.enums.SortDirection;
import com.sejong.projectservice.support.common.util.ExtractorUsername;
import com.sejong.projectservice.support.common.pagination.CursorPageRequest;
import com.sejong.projectservice.support.common.pagination.CursorPageResponse;
import com.sejong.projectservice.support.common.pagination.CustomPageRequest;
import com.sejong.projectservice.support.common.pagination.OffsetPageResponse;
import com.sejong.projectservice.domains.news.domain.NewsDto;
import com.sejong.projectservice.domains.user.UserIds;
import com.sejong.projectservice.domains.news.kafka.NewsEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Transactional(readOnly = true)
@Slf4j
public class NewsService {

    private final ArchiveRepository archiveRepository;
    private final UserExternalService userExternalService;
    private final NewsEventPublisher newsEventPublisher;

    @Transactional
    public NewsResDto createNews(NewsReqDto newsReqDto) {
        userExternalService.validateExistence(
                newsReqDto.getWriterUsername(),
                newsReqDto.getParticipantIds()
        );

        NewsDto newsDto = NewsAssembler.toNews(newsReqDto);
        NewsEntity entity = NewsMapper.toEntity(newsDto);
        NewsEntity savedNewsEntity = archiveRepository.save(entity);
        NewsDto dto = NewsMapper.toDomain(savedNewsEntity);
        newsEventPublisher.publishCreated(dto);

        return resolveUsernames(dto);
    }

    @Transactional
    public NewsResDto updateNews(Long newsId, NewsReqDto newsReqDto, String writerId) {
        NewsEntity newsEntity = archiveRepository.findById(newsId)
                .orElseThrow(() -> new BaseException(ExceptionType.NEWS_NOT_FOUND));
        newsEntity.validateOwner(writerId);

        newsEntity.update(
                ContentEmbeddable.of(NewsAssembler.toContent(newsReqDto)),
                UserIds.of(newsReqDto.getParticipantIds()).toString(),
                String.join(",", newsReqDto.getTags())
        );

        NewsDto dto = NewsMapper.toDomain(newsEntity);
        newsEventPublisher.publishUpdated(dto);

        return resolveUsernames(dto);
    }

    @Transactional
    public void deleteNews(Long newsId, String writerId) {
        NewsEntity newsEntity = archiveRepository.findById(newsId)
                .orElseThrow(() -> new BaseException(ExceptionType.NEWS_NOT_FOUND));
        newsEntity.validateOwner(writerId);

        archiveRepository.deleteById(newsEntity.getId());
        newsEventPublisher.publishDeleted(newsId);
    }

    public NewsResDto findById(Long newsId) {
        NewsEntity newsEntity = archiveRepository.findById(newsId)
                .orElseThrow(() -> new BaseException(ExceptionType.NEWS_NOT_FOUND));

        return resolveUsernames(NewsMapper.toDomain(newsEntity));
    }

    @Transactional(readOnly = true)
    public OffsetPageResponse<List<NewsResDto>> getOffsetNews(OffsetPageReqDto offsetPageReqDto) {
        CustomPageRequest pageRequest = offsetPageReqDto.toPageRequest();

        Pageable pageable = PageRequest.of(pageRequest.getPage(),
                pageRequest.getSize(),
                Sort.Direction.valueOf(pageRequest.getDirection().name()),
                pageRequest.getSortBy());

        Page<NewsEntity> archiveEntities = archiveRepository.findAll(pageable);

        List<NewsDto> archives = archiveEntities.stream()
                .map(NewsMapper::toDomain)
                .toList();

        OffsetPageResponse<List<NewsDto>> newsPage = OffsetPageResponse.ok(archiveEntities.getNumber(), archiveEntities.getTotalPages(), archives);

        List<NewsResDto> dtoList = newsPage.getData().stream()
                .map(this::resolveUsernames)
                .toList();

        return OffsetPageResponse.ok(newsPage.getPage(), newsPage.getTotalPage(), dtoList);
    }

    @Transactional(readOnly = true)
    public CursorPageResponse<List<NewsResDto>> getCursorNews(CursorPageReqDto cursorPageReqDto) {
        CursorPageRequest pageRequest = cursorPageReqDto.toPageRequest();

        Pageable pageable = PageRequest.of(0, pageRequest.getSize() + 1);
        List<NewsEntity> entities = getCursorBasedEntities(pageRequest, pageable);

        // 실제 요청한 크기보다 많이 조회되면 다음 페이지가 존재
        boolean hasNext = entities.size() > pageRequest.getSize();

        // 실제 반환할 데이터는 요청한 크기만큼만
        List<NewsEntity> resultEntities = hasNext ?
                entities.subList(0, pageRequest.getSize()) : entities; // Todo: 아예 sql로 limit

        List<NewsDto> newsDtos = resultEntities.stream()
                .map(NewsMapper::toDomain)
                .toList();

        // 다음 커서 계산
        Long nextCursor = hasNext && !newsDtos.isEmpty() ?
                newsDtos.get(newsDtos.size() - 1).getId() : null;

        CursorPageResponse<List<NewsDto>> newsPage = CursorPageResponse.ok(nextCursor, hasNext, newsDtos);

        List<NewsResDto> dtoList = newsPage.getContent().stream()
                .map(this::resolveUsernames)
                .toList();

        return CursorPageResponse.ok(newsPage.getNextCursor(), newsPage.isHasNext(), dtoList);
    }

    @Transactional(readOnly = true)
    public PostLikeCheckResponse checkNews(Long newsId) {
        boolean exists = archiveRepository.existsById(newsId);
        if (exists) {
            NewsEntity newsEntity = archiveRepository.findById(newsId)
                    .orElseThrow(() -> new BaseException(ExceptionType.NEWS_NOT_FOUND));
            return PostLikeCheckResponse.hasOfNews(NewsMapper.toDomain(newsEntity), true);
        }
        return PostLikeCheckResponse.hasNotOf();
    }

    private NewsResDto resolveUsernames(NewsDto newsDto) {
        List<String> usernames = ExtractorUsername.FromNewses(newsDto);
        log.info("usernames.size() {}",usernames.size());
        for(int i=0;i<usernames.size();i++){
            log.info("username.get({}) : {}",i,usernames.get(i));
        }
        Map<String, UserNameInfo> usernamesMap = userExternalService.getUserNameInfos(usernames);
        return NewsResDto.from(newsDto, usernamesMap);
    }

    @Transactional(readOnly = true)
    public Long getNewsCount() {
        Long count = archiveRepository.getNewsCount();
        return count;
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