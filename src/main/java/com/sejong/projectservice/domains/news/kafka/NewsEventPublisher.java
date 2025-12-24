package com.sejong.projectservice.domains.news.kafka;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sejong.projectservice.domains.news.domain.NewsEntity;
import com.sejong.projectservice.domains.news.dto.NewsDto;
import com.sejong.projectservice.support.common.constants.Type;
import com.sejong.projectservice.support.common.exception.BaseException;
import com.sejong.projectservice.support.common.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.sejong.projectservice.support.common.constants.Type.*;


@Service
@RequiredArgsConstructor
public class NewsEventPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String NEWS_EVENTS = "news";

    public void publishCreated(NewsEntity newsEntity){
        publish(newsEntity, CREATED);
    }

    public void publishUpdated(NewsEntity newsEntity) {
        publish(newsEntity, UPDATED);
    }

    public void publishDeleted(Long newsId) {
        NewsIndexEvent event = NewsIndexEvent.deleteOf(newsId.toString(), DELETED, System.currentTimeMillis());
        kafkaTemplate.send(NEWS_EVENTS, newsId.toString(),  toJsonString(event));
    }

    private void publish(NewsEntity newsEntity, Type type) {
        NewsIndexEvent event = NewsIndexEvent.of(newsEntity, type, System.currentTimeMillis());
        kafkaTemplate.send(NEWS_EVENTS, newsEntity.getId().toString(), toJsonString(event));
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
