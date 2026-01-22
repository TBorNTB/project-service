package com.sejong.projectservice.support.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(
        name = "outbox_event",
        indexes = {
                @Index(name = "idx_outbox_status_next", columnList = "status,nextAttemptAt"),
                @Index(name = "idx_outbox_locked_at", columnList = "lockedAt")
        }
)
@Getter
@NoArgsConstructor
public class OutboxEvent {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 128)
    private String aggregateType;

    @Column(nullable = false, length = 128)
    private String aggregateId;

    @Column(nullable = false, length = 128)
    private String eventType;

    @Column(nullable = false, length = 255)
    private String topic;

    @Column(nullable = false, length = 255)
    private String messageKey;

    @Lob
    @Column(nullable = false, columnDefinition = "longtext")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private OutboxStatus status;

    @Column(nullable = false)
    private int attempts;

    @Column(nullable = false)
    private Instant nextAttemptAt;

    @Column(length = 64)
    private String lockedBy;

    private Instant lockedAt;

    private Instant sentAt;

    @Column(length = 1000)
    private String lastError;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    @Version
    private long version;

    public static OutboxEvent pending(
            String aggregateType,
            String aggregateId,
            String eventType,
            String topic,
            String messageKey,
            String payload,
            Instant now
    ) {
        OutboxEvent e = new OutboxEvent();
        e.aggregateType = aggregateType;
        e.aggregateId = aggregateId;
        e.eventType = eventType;
        e.topic = topic;
        e.messageKey = messageKey;
        e.payload = payload;
        e.status = OutboxStatus.PENDING;
        e.attempts = 0;
        e.nextAttemptAt = now;
        return e;
    }

    public void claim(String instanceId, Instant now) {
        this.status = OutboxStatus.PROCESSING;
        this.lockedBy = instanceId;
        this.lockedAt = now;
    }

    public void markSent(Instant now) {
        this.status = OutboxStatus.SENT;
        this.sentAt = now;
        this.lockedBy = null;
        this.lockedAt = null;
        this.lastError = null;
    }

    public void markFailedAndRetry(String error, Instant nextAttemptAt) {
        this.status = OutboxStatus.PENDING;
        this.attempts = this.attempts + 1;
        this.nextAttemptAt = nextAttemptAt;
        this.lockedBy = null;
        this.lockedAt = null;
        this.lastError = truncate(error, 1000);
    }

    public void markDead(String error) {
        this.status = OutboxStatus.FAILED;
        this.lockedBy = null;
        this.lockedAt = null;
        this.lastError = truncate(error, 1000);
    }

    private static String truncate(String value, int maxLen) {
        if (value == null) {
            return null;
        }
        if (value.length() <= maxLen) {
            return value;
        }
        return value.substring(0, maxLen);
    }
}
