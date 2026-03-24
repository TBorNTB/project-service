CREATE TABLE cs_knowledge_attachment
(
    cs_knowledge_id  BIGINT       NOT NULL,
    file_key         VARCHAR(512) NOT NULL,
    original_file_name VARCHAR(255),
    CONSTRAINT fk_cs_knowledge_attachment FOREIGN KEY (cs_knowledge_id) REFERENCES cs_knowledge (id) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;