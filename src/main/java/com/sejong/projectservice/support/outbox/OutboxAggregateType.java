package com.sejong.projectservice.support.outbox;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OutboxAggregateType {

    PROJECT("project"),
    NEWS("news"),
    CS_KNOWLEDGE("cs-knowledge"),
    DOCUMENT("document"),
    CATEGORY("category"),
    ;

    private final String aggregateType;

}
