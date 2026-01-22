package com.sejong.projectservice.support.outbox;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxEventRepository repository;

    @Value("${app.outbox.max-attempts:100}")
    private int maxAttempts;

    @Value("${app.outbox.base-backoff-ms:1000}")
    private long baseBackoffMs;

    @Value("${app.outbox.max-backoff-ms:60000}")
    private long maxBackoffMs;

    @Transactional(propagation = Propagation.MANDATORY)
    public void enqueue(String aggregateType, String aggregateId, String eventType, String topic, String messageKey, String payload) {
        Instant now = Instant.now();
        OutboxEvent event = OutboxEvent.pending(aggregateType, aggregateId, eventType, topic, messageKey, payload, now);
        repository.save(event);
    }

    @Transactional
    public void markSent(UUID id) {
        OutboxEvent event = repository.findById(id).orElse(null);
        if (event == null) {
            return;
        }
        event.markSent(Instant.now());
    }

    @Transactional
    public void markFailure(UUID id, Exception exception) {
        OutboxEvent event = repository.findById(id).orElse(null);
        if (event == null) {
            return;
        }

        String error = exception == null ? "unknown" : exception.toString();
        if (event.getAttempts() + 1 >= maxAttempts) {
            event.markDead(error);
            return;
        }

        Instant now = Instant.now();
        Duration backoff = computeBackoff(event.getAttempts());
        event.markFailedAndRetry(error, now.plus(backoff));
    }

    private Duration computeBackoff(int attemptsSoFar) {
        long multiplier;
        if (attemptsSoFar <= 0) {
            multiplier = 1;
        } else {
            multiplier = 1L << Math.min(attemptsSoFar, 10);
        }

        long ms = Math.min(maxBackoffMs, baseBackoffMs * multiplier);
        return Duration.ofMillis(ms);
    }

    @Transactional
    public List<OutboxEvent> claimBatch(String instanceId, int batchSize, Duration processingTimeout) {
        Instant now = Instant.now();
        Instant staleBefore = now.minus(processingTimeout);

        List<OutboxEvent> events = repository.findClaimableForUpdate(now, staleBefore, PageRequest.of(0, batchSize));
        for (OutboxEvent event : events) {
            event.claim(instanceId, now);
        }
        return events;
    }
}
