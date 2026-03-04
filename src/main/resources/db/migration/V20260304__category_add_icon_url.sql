-- category 테이블에 아이콘 URL 컬럼 추가
ALTER TABLE category
    ADD COLUMN icon_url VARCHAR(512) NULL AFTER content;
