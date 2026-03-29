package com.sejong.projectservice.domains.csknowledge.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CsKnowledgeReferenceLink {

    @Column(name = "url", length = 2048, nullable = false)
    private String url;
}
