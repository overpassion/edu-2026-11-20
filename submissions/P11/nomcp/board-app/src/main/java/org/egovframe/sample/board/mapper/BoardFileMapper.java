package org.egovframe.sample.board.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.egovframe.sample.board.model.BoardFile;

import java.util.List;

/**
 * 첨부파일 CRUD MyBatis Mapper. SQL 정의는 {@code resources/mapper/BoardFileMapper.xml}.
 */
@Mapper
public interface BoardFileMapper {

    /** 게시글의 첨부파일 목록 */
    List<BoardFile> selectFileList(@Param("boardId") String boardId);

    /** 첨부파일 단건 조회 */
    BoardFile selectFile(@Param("fileId") String fileId);

    /** 첨부파일 등록 (SEQ_BOARD_FILE로 fileId 자동 발급) */
    int insertFile(BoardFile file);

    /** 게시글 소속 첨부파일 전체 삭제 */
    int deleteFileByBoard(@Param("boardId") String boardId);

    /** 첨부파일 단건 삭제 */
    int deleteFile(@Param("fileId") String fileId);
}
