package org.egovframe.sample.board;

import org.egovframe.sample.board.model.Board;
import org.egovframe.sample.board.model.BoardFile;
import org.egovframe.sample.board.model.BoardSearch;
import org.egovframe.sample.board.service.BoardService;
import org.egovframe.sample.board.service.FileStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 게시글 CRUD + 첨부파일 저장/조회/삭제에 대한 종단 간(smoke) 테스트.
 * 내장 HSQLDB 가 기동 시점의 schema.sql/data.sql 로 초기화된 상태에서 동작한다.
 */
@SpringBootTest
class EgovBoardApplicationTests {

    @Autowired
    private BoardService boardService;

    @Autowired
    private FileStorageService fileStorage;

    @Test
    void contextLoadsAndSeedDataPresent() {
        BoardSearch search = new BoardSearch();
        // data.sql 시드 5건
        assertTrue(boardService.countBoardList(search) >= 5, "시드 데이터가 로드되어야 한다");
        List<Board> list = boardService.getBoardList(search);
        assertFalse(list.isEmpty());
    }

    @Test
    void createReadDownloadAndDeleteWithAttachment() throws Exception {
        // given : 첨부 1개를 동반한 신규 글
        Board board = new Board();
        board.setTitle("테스트 제목");
        board.setContent("테스트 내용");
        board.setWriter("tester");

        MultipartFile file = new MockMultipartFile(
                "files", "report.txt", "text/plain",
                "hello-egov".getBytes(StandardCharsets.UTF_8));

        // when : 등록
        Board saved = boardService.addBoard(board, new MultipartFile[]{file});

        // then : 시퀀스 기반 boardId 발급 + 상세 조회에 첨부 노출
        assertNotNull(saved.getBoardId(), "boardId 가 시퀀스로 발급되어야 한다");
        Board detail = boardService.getBoard(saved.getBoardId());
        assertNotNull(detail);
        assertEquals("테스트 제목", detail.getTitle());
        List<BoardFile> files = detail.getFileList();
        assertEquals(1, files.size(), "첨부 1건이 저장되어야 한다");

        BoardFile meta = files.get(0);
        assertEquals("report.txt", meta.getFileNm());
        assertEquals("hello-egov".length(), meta.getFileSize());

        // 물리 파일 존재 확인
        Path physical = fileStorage.resolve(meta);
        assertTrue(Files.exists(physical), "업로드한 물리 파일이 존재해야 한다");
        assertEquals("hello-egov", Files.readString(physical, StandardCharsets.UTF_8));

        // 단건 첨부 조회 가능
        assertNotNull(boardService.getBoardFile(meta.getFileId()));

        // when : 게시글 삭제
        boardService.removeBoard(saved.getBoardId());

        // then : 게시글/첨부 메타/물리 파일 모두 제거
        assertNull(boardService.getBoard(saved.getBoardId()), "삭제 후 상세는 null");
        assertFalse(Files.exists(physical), "삭제 후 물리 파일도 제거되어야 한다");
    }
}
