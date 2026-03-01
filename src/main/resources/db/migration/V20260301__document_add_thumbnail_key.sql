-- document: thumbnail_url -> thumbnail_key (S3 key 저장, project/news/cs_knowledge와 동일)
ALTER TABLE document ADD COLUMN thumbnail_key VARCHAR(255) NULL;
ALTER TABLE document DROP COLUMN thumbnail_url;
