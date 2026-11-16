# board-app — eGovFrame 5.0 Boot 게시판 (첨부파일 포함)

MCP 코드 생성 도구(`egovframe-codegen`)로 생성한 **eGovFrame 5.0 Spring Boot** 게시판입니다.
과제 요구사항: Java 17 + Maven, 내장 HSQL DB 기동, 게시물 첨부파일 기능.

## 실행

```bash
mvn clean package -DskipTests        # 또는 mvn clean package
java -jar target/board-app-1.0.0.jar # 또는 mvn spring-boot:run
```

기동 후 <http://localhost:8080/> 접속 (목록), 게시글 등록 화면에서 파일 첨부가 가능합니다.

- 기본 프로파일: `hsql` (`spring.profiles.active=hsql`) — `jdbc:hsqldb:mem:testdb` 임베디드
  기동 시 `src/main/resources/db/boarddb.sql` 을 자동 실행하여 `BOARD`, `IDS`,
  `COMTNFILE`, `COMTNFILEDETAIL`, `COMTECOPSEQ` 를 생성합니다. 인메모리이므로 재기동하면 데이터는 초기화됩니다.
- 첨부 파일 디스크 저장 경로: `Globals.fileStorePath=/tmp/egov-upload`
  (`src/main/resources/application.properties`) — 운영에서는 외부 마운트/NAS 경로로 변경하세요.
- 업로드 한도: 단일 10MB / 요청 합계 50MB, 도메인 정책 `egov.attach.maxFileSize=10MB`,
  차단 확장자 블랙리스트 기본 적용(화이트리스트는 `egov.attach.allowedExtensions` 로 켜는 방식).

## 테이블 / 코드 구성

| 레이어 | 파일 |
| --- | --- |
| DDL(HSQL 시드) | `src/main/resources/db/boarddb.sql` |
| 운영 DB 마이그레이션 | `src/main/resources/db/migrations/<dialect>/boarddb.sql`, `attach-file.sql` |
| 화면(Thymeleaf+KRDS) | `src/main/resources/templates/thymeleaf/board/egovBoardList.html`, `egovBoardRegister.html` |
| 컨트롤러 | `src/main/java/egovframework/example/board/web/EgovBoardController.java` |
| 서비스 | `…/board/service/EgovBoardService.java`, `…/service/impl/EgovBoardServiceImpl.java` |
| MyBatis | `…/service/impl/BoardMapper.java`, `src/main/resources/egovframework/sqlmap/board/mappers/EgovBoard_Board_SQL.xml` |
| 첨부 인프라 | `…/cmm/service/EgovFileMng*.java`, `…/cmm/web/EgovFileDownloadController.java`, `…/config/EgovConfigAttachFile.java` |

`BOARD` 는 과제 DDL 기준(`BOARD_ID`/`TITLE`/`CONTENT`/`WRITER`)이며 첨부 연결을 위해
표준 컬럼 `ATCH_FILE_ID CHAR(20)` 만 추가했습니다. `BOARD_ID` 는
`EgovTableIdGnrServiceImpl` 자동 채번(`BOARD-00001` 형태), 첨부 그룹 키도 자동 채번(`FILE_` + 15자)입니다.

## URL

| 기능 | 메서드 | 경로 |
| --- | --- | --- |
| 목록/검색/페이징 | GET | `/`, `/egovBoardList.do` |
| 등록 화면 | POST | `/addBoardView.do` |
| 등록(첨부 다건) | POST(multipart) | `/addBoard.do` |
| 상세·수정 화면 | POST | `/updateBoardView.do` |
| 수정(기존 첨부 그룹에 추가) | POST(multipart) | `/updateBoard.do` |
| 삭제 | POST | `/deleteBoard.do` |
| 첨부 1건 삭제 | POST | `/deleteBoardAttachFile.do` |
| 첨부 다운로드 | GET | `/cmm/fms/FileDown.do?atchFileId=…&fileSn=…` |

검색 파라미터는 화면 셀렉트 값과 동일하게 `searchCondition`(0=제목, 1=본문, 2=작성자),
`searchKeyword`, `pageIndex`(1 이상) 를 사용합니다. 컨트롤러에 범위 외 값 정규화 가드가 있어
`pageIndex=0` 이나 잘못된 `searchCondition` 도 500 없이 처리됩니다.

## 보안

Spring Security 기본 활성 — 보안 응답 헤더, CSRF 토큰 검증(폼 hidden / `X-CSRF-TOKEN` 헤더),
StrictHttpFirewall 기본, CORS 비활성. 토글은 `application.properties` 의 `egov.security.*` 항목을 보세요.
자세한 구조는 `docs/egovframe-architecture.md`, DB 방언 이식 가이드는 `docs/dialect-portability.md` 입니다.
