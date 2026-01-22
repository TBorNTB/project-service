package com.sejong.projectservice.support.outbox;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select e from OutboxEvent e
            where
                (e.status = com.sejong.projectservice.support.outbox.OutboxStatus.PENDING and e.nextAttemptAt <= :now)
                or
                (e.status = com.sejong.projectservice.support.outbox.OutboxStatus.PROCESSING and e.lockedAt is not null and e.lockedAt <= :staleBefore)
            order by e.createdAt asc
            """)
    List<OutboxEvent> findClaimableForUpdate(
            @Param("now") Instant now,
            @Param("staleBefore") Instant staleBefore,
            Pageable pageable
    );
}
