package org.egovframe.sample.board.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.egovframe.sample.board.model.Board;
import org.egovframe.sample.board.model.BoardSearch;

import java.util.List;

/**
 * 게시글 CRUD MyBatis Mapper. SQL 정의는 {@code resources/mapper/BoardMapper.xml}.
 */
@Mapper
public interface BoardMapper {

    /** 검색/페이징된 게시글 목록 */
    List<Board> selectBoardList(BoardSearch search);

    /** 검색 조건에 해당하는 게시글 총 건수 */
    int countBoardList(BoardSearch search);

    /** 게시글 단건 조회 */
    Board selectBoard(@Param("boardId") String boardId);

    /** 게시글 등록 (SEQ_BOARD로 boardId 자동 발급) */
    int insertBoard(Board board);

    /** 게시글 수정 */
    int updateBoard(Board board);

    /** 게시글 삭제 */
    int deleteBoard(@Param("boardId") String boardId);

    /** 조회수 증가 */
    int updateHit(@Param("boardId") String boardId);
}
