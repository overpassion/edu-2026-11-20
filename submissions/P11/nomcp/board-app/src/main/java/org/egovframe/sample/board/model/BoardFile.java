package org.egovframe.sample.board.model;

import java.time.LocalDateTime;

/**
 * 첨부파일 도메인 모델 (BOARD_FILE_ATT 테이블 매핑).
 */
public class BoardFile {

    /** 첨부파일 ID (SEQ_BOARD_FILE 시퀀스로 발급) */
    private String fileId;

    /** 소속 게시글 ID */
    private String boardId;

    /** 원래 파일명 (사용자가 업로드한 이름) */
    private String fileNm;

    /** 서버 저장 파일명 (UUID 등) */
    private String filePath;

    /** 파일 크기(byte) */
    private long fileSize;

    /** 등록일 */
    private LocalDateTime regDt;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    public String getFileNm() {
        return fileNm;
    }

    public void setFileNm(String fileNm) {
        this.fileNm = fileNm;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public LocalDateTime getRegDt() {
        return regDt;
    }

    public void setRegDt(LocalDateTime regDt) {
        this.regDt = regDt;
    }
}
