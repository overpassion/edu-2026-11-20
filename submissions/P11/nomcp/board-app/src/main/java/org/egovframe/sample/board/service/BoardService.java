package org.egovframe.sample.board.service;

import org.egovframe.sample.board.model.Board;
import org.egovframe.sample.board.model.BoardFile;
import org.egovframe.sample.board.model.BoardSearch;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 게시판 서비스 인터페이스.
 */
public interface BoardService {

    /** 검색/페이징된 목록 (+ 총 건수) */
    List<Board> getBoardList(BoardSearch search);

    /** 검색 조건 총 건수 */
    int countBoardList(BoardSearch search);

    /** 게시글 상세 (첨부파일 포함, 조회수 증가) */
    Board getBoard(String boardId);

    /** 게시글 등록 (multipart 첨부 포함) */
    Board addBoard(Board board, MultipartFile[] files);

    /** 게시글 수정 (기존 첨부 유지/삭제 + 신규 첨부) */
    void modifyBoard(Board board, MultipartFile[] files, List<String> deletedFileIds);

    /** 게시글 삭제 (첨부파일 물리/논리 삭제 포함) */
    void removeBoard(String boardId);

    /** 첨부파일 단건 조회 (다운로드용) */
    BoardFile getBoardFile(String fileId);
}
