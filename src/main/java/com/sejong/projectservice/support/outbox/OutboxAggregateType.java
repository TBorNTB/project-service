package com.sejong.projectservice.support.outbox;

import com.sejong.projectservice.support.common.constants.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OutboxAggregateType {

    PROJECT("project"),
    NEWS("news"),
    CS_KNOWLEDGE("cs-knowledge"),
    DOCUMENT("document");

    private final String aggregateType;

}
