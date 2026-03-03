-- outbox_event: topic, message_key 제거 (aggregateType=topic, aggregateId=messageKey로 사용)
ALTER TABLE outbox_event
    DROP COLUMN topic,
    DROP COLUMN message_key;
