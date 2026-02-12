package com.sejong.projectservice.domains.news.kafka;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sejong.projectservice.domains.news.domain.NewsEntity;
import com.sejong.projectservice.support.common.constants.Type;
import com.sejong.projectservice.support.common.exception.BaseException;
import com.sejong.projectservice.support.common.exception.ExceptionType;
import com.sejong.projectservice.support.common.file.FileUploader;
import com.sejong.projectservice.support.outbox.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.sejong.projectservice.support.common.constants.Type.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class NewsEventPublisher {
    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;
    private final FileUploader fileUploader;
    private final String NEWS_EVENTS = "news";

    public void publishCreated(NewsEntity newsEntity){
        publish(newsEntity, CREATED);
        log.info("news 발행");
    }

    public void publishUpdated(NewsEntity newsEntity) {
        publish(newsEntity, UPDATED);
    }

    public void publishDeleted(Long newsId) {
        NewsIndexEvent event = NewsIndexEvent.deleteOf(newsId.toString(), DELETED, System.currentTimeMillis());
        outboxService.enqueue("news", newsId.toString(), "NewsDeleted", NEWS_EVENTS, newsId.toString(), toJsonString(event));
    }

    private void publish(NewsEntity newsEntity, Type type) {
        NewsIndexEvent event = NewsIndexEvent.of(newsEntity, fileUploader, type, System.currentTimeMillis());
        outboxService.enqueue("news", newsEntity.getId().toString(), "News" + type.name(), NEWS_EVENTS, newsEntity.getId().toString(), toJsonString(event));
    }

    private String toJsonString(Object object) {
        try {
            String message = objectMapper.writeValueAsString(object);
            return message;
        } catch (JsonProcessingException e) {
            throw new BaseException(ExceptionType.JSON_PARSHING_ERROR);
        }
    }
}
