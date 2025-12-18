package com.sejong.archiveservice.infrastructure.news.kafka;

import static com.sejong.archiveservice.infrastructure.common.Type.CREATED;
import static com.sejong.archiveservice.infrastructure.common.Type.UPDATED;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sejong.archiveservice.core.news.News;
import com.sejong.archiveservice.infrastructure.common.Type;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewsEventPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String NEWS_EVENTS = "news";

    public void publishCreated(News news){
        publish(news, CREATED);
    }

    public void publishUpdated(News news) {
        publish(news, UPDATED);
    }

    public void publishDeleted(Long newsId) {
        NewsIndexEvent event = NewsIndexEvent.deleteOf(newsId.toString(), Type.DELETED, System.currentTimeMillis());
        kafkaTemplate.send(NEWS_EVENTS, newsId.toString(),  toJsonString(event));
    }

    private void publish(News news, Type type) {
        NewsIndexEvent event = NewsIndexEvent.of(news, type, System.currentTimeMillis());
        kafkaTemplate.send(NEWS_EVENTS, news.getId().toString(), toJsonString(event));
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
