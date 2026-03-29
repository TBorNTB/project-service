-- 참고 링크 다건 저장 (순서 유지)
CREATE TABLE cs_knowledge_reference_link
(
    cs_knowledge_id BIGINT        NOT NULL,
    sort_order      INT           NOT NULL,
    url             VARCHAR(2048) NOT NULL,
    PRIMARY KEY (cs_knowledge_id, sort_order),
    CONSTRAINT fk_cs_knowledge_reference_link FOREIGN KEY (cs_knowledge_id) REFERENCES cs_knowledge (id) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 기존 단일 reference_link 컬럼 데이터 이전
INSERT INTO cs_knowledge_reference_link (cs_knowledge_id, sort_order, url)
SELECT id, 0, reference_link
FROM cs_knowledge
WHERE reference_link IS NOT NULL
  AND TRIM(reference_link) <> '';

ALTER TABLE cs_knowledge
    DROP COLUMN reference_link;
