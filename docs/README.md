# Project Service - PlantUML 다이어그램

이 폴더에는 Project Service의 아키텍처와 구조를 보여주는 PlantUML 다이어그램들이 포함되어 있습니다.

## 다이어그램 목록

### 1. `architecture.puml` - 전체 아키텍처
- 시스템의 전체적인 구조와 레이어별 구성
- 외부 서비스와의 연동 관계
- 데이터베이스 연결 구조

### 2. `projectDto-class.puml` - Project 도메인 클래스 구조
- Project 관련 도메인 클래스들의 관계
- Application, Core, Infrastructure 레이어 구조
- 주요 메서드와 속성

### 3. `projectDto-creation-sequence.puml` - 프로젝트 생성 시퀀스
- 프로젝트 생성 과정의 상세한 흐름
- 각 컴포넌트 간의 상호작용
- 데이터 저장 및 캐시 갱신 과정

### 4. `database-erd.puml` - 데이터베이스 ERD
- 주요 테이블 구조
- 테이블 간의 관계
- 필드 타입과 제약사항

## PlantUML 사용법

### VS Code에서 사용하기

1. **PlantUML 확장 프로그램 설치**
   - VS Code 마켓플레이스에서 `PlantUML` 검색
   - `jebbs.plantuml` 확장 프로그램 설치

2. **Java 설치 확인**
   - PlantUML은 Java를 사용하므로 Java 8 이상 설치 필요
   - `java -version` 명령어로 확인

3. **다이어그램 보기**
   - `.puml` 파일을 열고 `Alt + Shift + D` (또는 `Cmd + Shift + D`)
   - 또는 우클릭 후 "Preview Current Diagram" 선택

### 온라인에서 사용하기

1. [PlantUML Online Server](http://www.plantuml.com/plantuml/uml/) 접속
2. `.puml` 파일 내용을 복사하여 붙여넣기
3. 자동으로 다이어그램이 렌더링됨

### 이미지로 내보내기

VS Code PlantUML 확장 프로그램에서:
- `Ctrl + Shift + P` (또는 `Cmd + Shift + P`)
- "PlantUML: Export Current Diagram"
- 원하는 형식 선택 (PNG, SVG, PDF 등)

## 다이어그램 수정 및 추가

새로운 다이어그램을 추가하거나 기존 다이어그램을 수정할 때:

1. `.puml` 파일 생성/수정
2. PlantUML 문법에 맞게 작성
3. VS Code에서 미리보기로 확인
4. 필요시 이미지로 내보내기

## PlantUML 문법 참고

- [PlantUML 공식 문서](https://plantuml.com/)
- [PlantUML 언어 레퍼런스](https://plantuml.com/guide)
- [PlantUML 예제 모음](https://plantuml.com/examples)

## 주의사항

- 모든 `.puml` 파일은 UTF-8 인코딩으로 저장
- 한글 텍스트 사용 가능
- 다이어그램 수정 시 Git에 커밋하여 버전 관리

