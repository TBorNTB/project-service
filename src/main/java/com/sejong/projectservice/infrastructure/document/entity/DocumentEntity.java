package com.sejong.projectservice.infrastructure.document.entity;

import com.sejong.projectservice.infrastructure.project.entity.ProjectEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "document")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class DocumentEntity {

  @Id
  @Column(name = "document_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "yorkie_document_id", nullable = false, unique = true)
  private String yorkieDocumentId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "project_id", nullable = false)
  private ProjectEntity projectEntity;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  private String title;
  private String description;
  private String thumbnailUrl;

  @Column(columnDefinition = "TEXT")
  private String content;
}
