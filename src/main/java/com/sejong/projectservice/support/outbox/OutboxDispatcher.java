package com.sejong.projectservice.support.outbox;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxDispatcher {

    private final OutboxService outboxService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private final String instanceId = UUID.randomUUID().toString();

    @Value("${app.outbox.enabled:true}")
    private boolean enabled;

    @Value("${app.outbox.batch-size:20}")
    private int batchSize;

    @Value("${app.outbox.processing-timeout-ms:120000}")
    private long processingTimeoutMs;

    @Value("${app.outbox.send-timeout-ms:3000}")
    private long sendTimeoutMs;

    @Scheduled(fixedDelayString = "${app.outbox.poll-interval-ms:2000}")
    public void dispatch() {
        if (!enabled) {
            return;
        }

        List<OutboxEvent> claimed = outboxService.claimBatch(instanceId, batchSize, Duration.ofMillis(processingTimeoutMs));
        for (OutboxEvent event : claimed) {
            UUID id = event.getId();
            try {
                kafkaTemplate.send(event.getTopic(), event.getMessageKey(), event.getPayload())
                        .get(sendTimeoutMs, TimeUnit.MILLISECONDS);
                outboxService.markSent(id);
            } catch (Exception ex) {
                log.warn("outbox dispatch failed id={}, topic={}, key={}", id, event.getTopic(), event.getMessageKey(), ex);
                outboxService.markFailure(id, ex);
            }
        }
    }
}
