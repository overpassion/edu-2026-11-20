package org.egovframe.sample.board.model;

/**
 * 게시판 목록 검색/페이징 조건.
 * <p>eGovFrame {@code PaginationInfo}와 함께 사용된다.</p>
 */
public class BoardSearch {

    /** 검색 유형: TITLE / CONTENT / WRITER (기본 TITLE) */
    private String searchTp = "TITLE";

    /** 검색어 */
    private String searchWrd = "";

    /** 조회 시작 인덱스(0-base) - PaginationInfo.getFirstRecordIndex() 로 계산 */
    private int firstRecordIndex = 0;

    /** 한 페이지당 게시글 수 */
    private int recordCountPerPage = 10;

    /** 페이지블록 내 페이지 수 */
    private int pageSize = 5;

    /** 현재 페이지 번호 */
    private int currentPageNo = 1;

    public String getSearchTp() {
        return (searchTp == null || searchTp.isBlank()) ? "TITLE" : searchTp;
    }

    public void setSearchTp(String searchTp) {
        this.searchTp = searchTp;
    }

    public String getSearchWrd() {
        return searchWrd == null ? "" : searchWrd;
    }

    public void setSearchWrd(String searchWrd) {
        this.searchWrd = searchWrd;
    }

    public int getFirstRecordIndex() {
        return firstRecordIndex;
    }

    public void setFirstRecordIndex(int firstRecordIndex) {
        this.firstRecordIndex = firstRecordIndex;
    }

    public int getRecordCountPerPage() {
        return recordCountPerPage;
    }

    public void setRecordCountPerPage(int recordCountPerPage) {
        this.recordCountPerPage = recordCountPerPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCurrentPageNo() {
        return currentPageNo < 1 ? 1 : currentPageNo;
    }

    public void setCurrentPageNo(int currentPageNo) {
        this.currentPageNo = currentPageNo;
    }
}
