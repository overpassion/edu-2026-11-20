-- ===================================================================
-- 더미 데이터 (기동 시 1회 자동 삽입)
-- 고정 ID(1~5) 사용 후 시퀀스를 그보다 큰 값으로 재시작하여
-- 이후 신규 등록 시 발행되는 ID와 충돌하지 않도록 한다.
-- ===================================================================

INSERT INTO BOARD (BOARD_ID, TITLE, CONTENT, WRITER, HIT) VALUES ('1', 'eGovFrame 5.0 게시판입니다', '전자정부 표준프레임워크 5.0 Boot + 내장 HSQLDB 예제입니다.', 'admin', 10);
INSERT INTO BOARD (BOARD_ID, TITLE, CONTENT, WRITER, HIT) VALUES ('2', '첨부파일 테스트 글', '업로드/다운로드 확인용 글입니다.', 'hong', 5);
INSERT INTO BOARD (BOARD_ID, TITLE, CONTENT, WRITER, HIT) VALUES ('3', 'Java 17 + Maven + MyBatis', 'MyBatis 기반 CRUD와 페이징, 검색을 지원합니다.', 'kim', 0);
INSERT INTO BOARD (BOARD_ID, TITLE, CONTENT, WRITER, HIT) VALUES ('4', '두번째 페이지 테스트', '페이징 동작 확인을 위한 여러번째 게시글입니다.', 'lee', 0);
INSERT INTO BOARD (BOARD_ID, TITLE, CONTENT, WRITER, HIT) VALUES ('5', '게시글 검색 테스트', '제목/내용/작성자 검색을 사용해 보세요.', 'park', 0);

-- 시퀀스 재시작 (기존 데이터 이후부터 ID 발행)
ALTER SEQUENCE SEQ_BOARD RESTART WITH 1000;
ALTER SEQUENCE SEQ_BOARD_FILE RESTART WITH 1000;
