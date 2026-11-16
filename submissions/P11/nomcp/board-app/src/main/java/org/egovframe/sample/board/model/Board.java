package org.egovframe.sample.board.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 게시글 도메인 모델 (BOARD 테이블 매핑).
 * 컬럼: BOARD_ID / TITLE / CONTENT / WRITER + 추가(HIT, REG_DT)
 */
public class Board {

    /** 게시글 ID (SEQ_BOARD 시퀀스로 발급, VARCHAR(20)) */
    private String boardId;

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 200, message = "제목은 200자 이하여야 합니다.")
    private String title;

    @Size(max = 4000, message = "내용은 4000자 이하여야 합니다.")
    private String content;

    @NotBlank(message = "작성자는 필수입니다.")
    @Size(max = 50, message = "작성자는 50자 이하여야 합니다.")
    private String writer;

    /** 조회수 */
    private int hit;

    /** 등록일 */
    private LocalDateTime regDt;

    /** 첨부파일 목록 (조회/등록 시 함께 처리) */
    private List<BoardFile> fileList;

    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public int getHit() {
        return hit;
    }

    public void setHit(int hit) {
        this.hit = hit;
    }

    public LocalDateTime getRegDt() {
        return regDt;
    }

    public void setRegDt(LocalDateTime regDt) {
        this.regDt = regDt;
    }

    public List<BoardFile> getFileList() {
        return fileList;
    }

    public void setFileList(List<BoardFile> fileList) {
        this.fileList = fileList;
    }
}
