package com.sejong.projectservice.core.document;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Document {

  private LocalDateTime updatedAt;
  private LocalDateTime createdAt;

  private Long id;
  private String yorkieDocumentId;

  private String title;
  private String content;
  private String description;
  private String thumbnailUrl;
}
