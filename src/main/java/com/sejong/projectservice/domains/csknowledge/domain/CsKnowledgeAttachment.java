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
public class CsKnowledgeAttachment {

    @Column(name = "file_key", length = 512)
    private String fileKey;

    @Column(name = "original_file_name", length = 255)
    private String originalFileName;
}