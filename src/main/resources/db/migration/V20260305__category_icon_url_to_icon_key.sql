-- category 아이콘: presigned URL 업로드용 key 저장으로 컬럼 변경
ALTER TABLE category
    CHANGE COLUMN icon_url icon_key VARCHAR(512) NULL;
