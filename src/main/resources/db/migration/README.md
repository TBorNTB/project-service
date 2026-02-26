# DB Migration (Flyway)

## 파일 네이밍 규칙

```
V{YYMMDD}__{설명}.sql
```

예) `V20260227__init.sql` → 2026년 2월 27일 초기 스키마

## 마이그레이션 추가 방법

1. `src/main/resources/db/migration/` 아래에 새 SQL 파일 생성
2. 버전 번호는 **반드시 기존 파일보다 커야** 함 (Flyway는 버전 순으로 실행)
3. 한 번 적용된 파일은 **절대 수정하지 말 것** (체크섬 불일치로 실패)
4. 컬럼 추가/변경은 새 버전 파일에 `ALTER TABLE` 로 작성

## 현재 마이그레이션 이력

| 버전      | 파일                  | 설명                 |
|---------|---------------------|--------------------|
| V260227 | V20260227__init.sql | 초기 스키마 (전체 테이블 생성) |

## 포함된 테이블

- `category` — 프로젝트/CS/Q&A 공통 카테고리
- `techstack` — 기술 스택
- `project` — 프로젝트 (계승 프로젝트: `parent_project_id`)
- `project_category` — 프로젝트 ↔ 카테고리 연결
- `project_techstack` — 프로젝트 ↔ 기술스택 연결
- `project_user` — 협력자 (CollaboratorEntity)
- `subgoal` — 프로젝트 하위 목표
- `document` — 프로젝트 문서
- `news` — 뉴스/소식
- `question` — Q&A 질문
- `question-answer` — Q&A 답변
- `question_category` — Q&A ↔ 카테고리 연결
- `cs_knowledge` — CS 지식 게시글
- `outbox_event` — 트랜잭셔널 아웃박스 (Kafka 발행 보장)
