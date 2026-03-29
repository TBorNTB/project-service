-- CS 지식 게시글 참고 링크 (외부 URL)
ALTER TABLE cs_knowledge
    ADD COLUMN reference_link VARCHAR(2048) NULL AFTER description;
