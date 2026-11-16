# board-app — eGovFrame 5.0 Boot 게시판

전자정부 표준프레임워크 **eGovFrame 5.0 Boot** 기반의 게시판 프로젝트입니다.
내장 **HSQLDB(in-memory)** 로 기동하며, 게시글에 **첨부파일 업로드/다운로드/삭제** 기능을 제공합니다.

## 요구사항 대응

| 과제 요구 | 구현 |
|---|---|
| eGovFrame 5.0 Boot 기반 | 부모 POM: `org.egovframe.boot:egovframe-boot-starter-parent:5.0.0` (Spring Boot 3.5.6 / Spring 6.2.11) |
| Java 17 | `<java.version>17</java.version>`, Temurin 17.0.15 로 빌드·실행 검증 |
| Maven | 단일-module Maven 프로젝트 (`mvn` 기동) |
| 내장 HSQL DB 로 기동 | `jdbc:hsqldb:mem:egovboard`, 기동 시 `schema.sql`/`data.sql` 자동 실행 |
| 첨부파일 기능 | 게시글 등록/수정 시 다건 업로드, 다운로드, 삭제(물리+메타) |
| 기준 DDL `BOARD` | `BOARD(BOARD_ID, TITLE, CONTENT, WRITER)` + `HIT`, `REG_DT` 추가, 첨부용 `BOARD_FILE_ATT` 추가 |

## 주요 기능
- 게시글 목록(검색: 제목/내용/작성자) + **페이징** — eGovFrame `PaginationInfo` 활용
- 게시글 등록 / 상세(조회수 자동 증가) / 수정 / 삭제
- **첨부파일**: 등록·수정 시 다건 업로드, 다운로드, 수정 시 기존 첨부 선택 삭제, 게시글 삭제 시 연쇄 삭제
- 게시글/첨부 ID는 **HSQLDB 시퀀스** + MyBatis `<selectKey>` 로 자동 발급 (BOARD_ID: VARCHAR(20))
- 서비스는 eGovFrame `EgovAbstractServiceImpl` 상속 (FDL-CMMN)

## 기술 구성
- Spring Boot Web(MVC) + Thymeleaf + Bean Validation
- MyBatis (`egovframe-rte-psl-dataaccess` + `mybatis-spring-boot-starter:3.0.4`)
- HSQLDB 2.7.4 (런타임, 인메모리)
- eGovFrame RTE: `fdl-cmmn`, `ptl-mvc`, `psl-dataaccess`

## 빌드 & 실행

```bash
# 실행 (내장 HSQLDB 로 자동 초기화 후 8080 포트 기동)
mvn spring-boot:run

# 또는 실행 가능한 jar 로
mvn -DskipTests package
java -jar target/board-app.jar

# 브라우저
open http://localhost:8080/board/list
```

### ⚠️ 의존성 저장소 관련 (중요)
eGovFrame 5.0 아티팩트(`org.egovframe.*`)는 **Maven Central 에 없고** eGovFrame 저장소
(`https://maven.egovframe.go.kr/maven/`, 저장소 id `egovframe`)에 있습니다.
`~/.m2` 에 캐시된 본을 Maven 이 사용하도록 `pom.xml` 에 저장소 id 를 `egovframe`/`egovframe2`
로 맞춰 두었습니다. 따라서:

- 이미 캐시된 이 환경에서는 추가 다운로드 없이 빌드됩니다.
- 캐시가 없는 클린 환경에서 외부 eGovFrame 저장소에 접근할 수 없다면, 아래처럼 **저장소 미러/설정**
  또는 **해당 아티팩트의 사전 `mvn install`** 이 필요합니다.

```bash
# (참고) 아티팩트가 ~/.m2 에 이미 있는 상태에서 원격 재조회를 막고자 할 때
mvn -o -DskipTests package
```

## 첨부파일 저장 위치
실행 디렉터리 기준 `egov.upload.dir`(기본 `./upload-files`) 아래에 저장됩니다.
(`application.properties` 에서 변경 가능, 없으면 자동 생성)

## 프로젝트 구조

```
board-app/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/org/egovframe/sample/board/
    │   │   ├── EgovBoardApplication.java        # 진입점(@MapperScan)
    │   │   ├── config/EgovCoreConfig.java       # messageSource, leaveaTrace Bean
    │   │   ├── model/                           # Board, BoardFile, BoardSearch
    │   │   ├── mapper/                          # BoardMapper, BoardFileMapper(@Mapper)
    │   │   ├── service/                         # BoardService/Impl, FileStorageService
    │   │   └── web/                             # BoardController, HomeController
    │   └── resources/
    │       ├── application.properties
    │       ├── db/{schema.sql, data.sql}
    │       ├── mapper/{BoardMapper.xml, BoardFileMapper.xml}
    │       ├── messages.properties
    │       ├── static/css/style.css
    │       └── templates/board/{list,detail,form}.html
    └── test/java/.../EgovBoardApplicationTests.java   # CRUD+첨부 smoke 테스트
```

## REST/화면 엔드포인트

| Method | Path | 설명 |
|---|---|---|
| GET  | `/` | `/board/list` 로 리다이렉트 |
| GET  | `/board/list` | 목록(검색/페이징: `page`, `searchTp`, `searchWrd`) |
| GET  | `/board/detail/{boardId}` | 상세(조회수 증가, 첨부 나열) |
| GET  | `/board/createForm` | 등록 폼 |
| POST | `/board/create` | 등록(multipart: `files`) |
| GET  | `/board/editForm/{boardId}` | 수정 폼 |
| POST | `/board/update` | 수정(multipart: `files`, `deletedFileIds`) |
| POST | `/board/delete/{boardId}` | 삭제(첨부 연쇄 삭제) |
| GET  | `/board/file/{fileId}` | 첨부 다운로드 |

## 테스트

```bash
mvn test
```

`EgovBoardApplicationTests` 가 컨텍스트 로딩, 시드 데이터, 첨부 업로드→다운로드→삭제
(물리 파일 포함)까지 검증합니다.
