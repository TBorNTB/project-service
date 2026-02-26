-- ============================================================
-- V20260227__init.sql
-- project-service 초기 스키마
-- Spring Boot 3.5.0 / Hibernate 6 / MySQL 8 기준
-- ============================================================

-- 1. category
CREATE TABLE category
(
    category_id BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(200),
    content     TEXT,
    PRIMARY KEY (category_id),
    UNIQUE KEY uk_category_name (name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 2. techstack
CREATE TABLE techstack
(
    techstack_id BIGINT       NOT NULL AUTO_INCREMENT,
    name         VARCHAR(255) NOT NULL,
    PRIMARY KEY (techstack_id),
    UNIQUE KEY uk_techstack_name (name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 3. project
CREATE TABLE project
(
    project_id        BIGINT       NOT NULL AUTO_INCREMENT,
    title             VARCHAR(255),
    description       VARCHAR(255),
    content           TEXT,
    username          VARCHAR(255),
    project_status    VARCHAR(50),
    started_at        DATETIME(6),
    ended_at          DATETIME(6),
    created_at        DATETIME(6),
    updated_at        DATETIME(6),
    thumbnail_key     VARCHAR(255),
    parent_project_id BIGINT,
    PRIMARY KEY (project_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 4. project_category
CREATE TABLE project_category
(
    project_category_id BIGINT NOT NULL AUTO_INCREMENT,
    project_id          BIGINT NOT NULL,
    category_id         BIGINT NOT NULL,
    PRIMARY KEY (project_category_id),
    CONSTRAINT fk_project_category_project FOREIGN KEY (project_id) REFERENCES project (project_id),
    CONSTRAINT fk_project_category_category FOREIGN KEY (category_id) REFERENCES category (category_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 5. project_techstack
CREATE TABLE project_techstack
(
    project_techstack_id BIGINT NOT NULL AUTO_INCREMENT,
    project_id           BIGINT NOT NULL,
    techstack_id         BIGINT NOT NULL,
    PRIMARY KEY (project_techstack_id),
    CONSTRAINT fk_project_techstack_project FOREIGN KEY (project_id) REFERENCES project (project_id),
    CONSTRAINT fk_project_techstack_techstack FOREIGN KEY (techstack_id) REFERENCES techstack (techstack_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 6. project_user (CollaboratorEntity)
CREATE TABLE project_user
(
    project_user_id   BIGINT NOT NULL AUTO_INCREMENT,
    collaborator_name VARCHAR(255),
    project_id        BIGINT NOT NULL,
    PRIMARY KEY (project_user_id),
    CONSTRAINT fk_project_user_project FOREIGN KEY (project_id) REFERENCES project (project_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 7. subgoal
CREATE TABLE subgoal
(
    id         BIGINT NOT NULL AUTO_INCREMENT,
    content    VARCHAR(255),
    completed  BIT(1),
    created_at DATETIME(6),
    updated_at DATETIME(6),
    project_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_subgoal_project FOREIGN KEY (project_id) REFERENCES project (project_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 8. document
CREATE TABLE document
(
    document_id   BIGINT       NOT NULL AUTO_INCREMENT,
    title         VARCHAR(255) NOT NULL,
    description   VARCHAR(255) NOT NULL,
    thumbnail_url VARCHAR(255),
    content       TEXT,
    created_at    DATETIME(6),
    updated_at    DATETIME(6),
    project_id    BIGINT       NOT NULL,
    PRIMARY KEY (document_id),
    CONSTRAINT fk_document_project FOREIGN KEY (project_id) REFERENCES project (project_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 9. news  (ContentEmbeddable: title, summary, content, category)
CREATE TABLE news
(
    news_id       BIGINT       NOT NULL AUTO_INCREMENT,
    title         VARCHAR(255) NOT NULL,
    summary       VARCHAR(255) NOT NULL,
    content       VARCHAR(255) NOT NULL,
    category      VARCHAR(255) NOT NULL,
    thumbnail_key VARCHAR(255),
    writer_id     VARCHAR(255) NOT NULL,
    news_user_ids VARCHAR(255),
    tag           VARCHAR(255),
    created_at    DATETIME(6)  NOT NULL,
    updated_at    DATETIME(6)  NOT NULL,
    PRIMARY KEY (news_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 10. question
CREATE TABLE question
(
    id              BIGINT NOT NULL AUTO_INCREMENT,
    title           VARCHAR(255),
    description     VARCHAR(255),
    content         TEXT,
    username        VARCHAR(255),
    question_status VARCHAR(50),
    created_at      DATETIME(6),
    updated_at      DATETIME(6),
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 11. question-answer  (테이블명에 하이픈 포함)
CREATE TABLE `question-answer`
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    content     TEXT,
    username    VARCHAR(255) NOT NULL,
    accepted    BIT(1),
    created_at  DATETIME(6),
    updated_at  DATETIME(6),
    question_id BIGINT       NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_question_answer_question FOREIGN KEY (question_id) REFERENCES question (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 12. question_category
CREATE TABLE question_category
(
    question_category_id BIGINT NOT NULL AUTO_INCREMENT,
    question_id          BIGINT NOT NULL,
    category_id          BIGINT NOT NULL,
    PRIMARY KEY (question_category_id),
    UNIQUE KEY uk_question_category (question_id, category_id),
    CONSTRAINT fk_question_category_question FOREIGN KEY (question_id) REFERENCES question (id),
    CONSTRAINT fk_question_category_category FOREIGN KEY (category_id) REFERENCES category (category_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 13. cs_knowledge
CREATE TABLE cs_knowledge
(
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    title         VARCHAR(255) NOT NULL,
    writer_id     VARCHAR(255) NOT NULL,
    content       LONGTEXT,
    description   VARCHAR(255),
    thumbnail_key VARCHAR(255),
    created_at    DATETIME(6),
    updated_at    DATETIME(6),
    category_id   BIGINT       NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_cs_knowledge_category_id (category_id, id),
    CONSTRAINT fk_cs_knowledge_category FOREIGN KEY (category_id) REFERENCES category (category_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 14. outbox_event
CREATE TABLE outbox_event
(
    id              BINARY(16)    NOT NULL,
    aggregate_type  VARCHAR(128)  NOT NULL,
    aggregate_id    VARCHAR(128)  NOT NULL,
    event_type      VARCHAR(128)  NOT NULL,
    topic           VARCHAR(255)  NOT NULL,
    message_key     VARCHAR(255)  NOT NULL,
    payload         LONGTEXT      NOT NULL,
    status          VARCHAR(32)   NOT NULL,
    attempts        INT           NOT NULL,
    next_attempt_at DATETIME(6)   NOT NULL,
    locked_by       VARCHAR(64),
    locked_at       DATETIME(6),
    sent_at         DATETIME(6),
    last_error      VARCHAR(1000),
    created_at      DATETIME(6)   NOT NULL,
    updated_at      DATETIME(6)   NOT NULL,
    version         BIGINT,
    PRIMARY KEY (id),
    INDEX idx_outbox_status_next (status, next_attempt_at),
    INDEX idx_outbox_locked_at (locked_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
