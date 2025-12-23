package com.sejong.projectservice.domains.news.kafka;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sejong.projectservice.domains.news.dto.NewsDto;
import com.sejong.projectservice.support.common.constants.Type;
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

    public void publishCreated(NewsDto newsDto){
        publish(newsDto, CREATED);
    }

    public void publishUpdated(NewsDto newsDto) {
        publish(newsDto, UPDATED);
    }

    public void publishDeleted(Long newsId) {
        NewsIndexEvent event = NewsIndexEvent.deleteOf(newsId.toString(), DELETED, System.currentTimeMillis());
        kafkaTemplate.send(NEWS_EVENTS, newsId.toString(),  toJsonString(event));
    }

    private void publish(NewsDto newsDto, Type type) {
        NewsIndexEvent event = NewsIndexEvent.of(newsDto, type, System.currentTimeMillis());
        kafkaTemplate.send(NEWS_EVENTS, newsDto.getId().toString(), toJsonString(event));
    }

    private String toJsonString(Object object) {
        try {
            String message = objectMapper.writeValueAsString(object);
            return message;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Json 직렬화 실패: " + e.getMessage(), e);
        }
    }
}
