package org.egovframe.sample.board.service;

import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.egovframe.sample.board.mapper.BoardFileMapper;
import org.egovframe.sample.board.mapper.BoardMapper;
import org.egovframe.sample.board.model.Board;
import org.egovframe.sample.board.model.BoardFile;
import org.egovframe.sample.board.model.BoardSearch;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 게시판 서비스 구현.
 * <p>eGovFrame {@link EgovAbstractServiceImpl} 을 상속해 {@code egovLogger} 를 사용한다.</p>
 */
@Service
public class BoardServiceImpl extends EgovAbstractServiceImpl implements BoardService {

    private final BoardMapper boardMapper;
    private final BoardFileMapper boardFileMapper;
    private final FileStorageService fileStorage;

    public BoardServiceImpl(BoardMapper boardMapper,
                            BoardFileMapper boardFileMapper,
                            FileStorageService fileStorage) {
        this.boardMapper = boardMapper;
        this.boardFileMapper = boardFileMapper;
        this.fileStorage = fileStorage;
    }

    @Override
    public List<Board> getBoardList(BoardSearch search) {
        return boardMapper.selectBoardList(search);
    }

    @Override
    public int countBoardList(BoardSearch search) {
        return boardMapper.countBoardList(search);
    }

    @Override
    @Transactional
    public Board getBoard(String boardId) {
        boardMapper.updateHit(boardId);               // 조회 시 조회수 증가
        Board board = boardMapper.selectBoard(boardId);
        if (board != null) {
            board.setFileList(boardFileMapper.selectFileList(boardId));
        }
        return board;
    }

    @Override
    @Transactional
    public Board addBoard(Board board, MultipartFile[] files) {
        boardMapper.insertBoard(board);               // selectKey 로 boardId 발급
        egovLogger.debug("board inserted: id={}", board.getBoardId());

        saveFiles(board.getBoardId(), files);
        return board;
    }

    @Override
    @Transactional
    public void modifyBoard(Board board, MultipartFile[] files, List<String> deletedFileIds) {
        boardMapper.updateBoard(board);

        // 명시적으로 삭제 처리된 기존 첨부
        if (!CollectionUtils.isEmpty(deletedFileIds)) {
            for (String fileId : deletedFileIds) {
                BoardFile exist = boardFileMapper.selectFile(fileId);
                if (exist != null) {
                    boardFileMapper.deleteFile(fileId);
                    fileStorage.delete(exist);
                }
            }
        }
        // 새 첨부 추가
        saveFiles(board.getBoardId(), files);
        egovLogger.debug("board modified: id={}", board.getBoardId());
    }

    @Override
    @Transactional
    public void removeBoard(String boardId) {
        List<BoardFile> files = boardFileMapper.selectFileList(boardId);
        boardFileMapper.deleteFileByBoard(boardId);   // 메타데이터 삭제
        files.forEach(fileStorage::delete);           // 물리 파일 삭제
        boardMapper.deleteBoard(boardId);             // 게시글 삭제
        egovLogger.debug("board removed: id={}", boardId);
    }

    @Override
    public BoardFile getBoardFile(String fileId) {
        return boardFileMapper.selectFile(fileId);
    }

    /** MultipartFile[] 을 저장소에 쓰고 메타데이터를 DB 에 삽입한다. */
    private void saveFiles(String boardId, MultipartFile[] files) {
        if (files == null) {
            return;
        }
        List<BoardFile> saved = new ArrayList<>();
        for (MultipartFile mf : files) {
            if (mf == null || mf.isEmpty()) {
                continue;
            }
            try {
                BoardFile meta = fileStorage.store(boardId, mf);
                boardFileMapper.insertFile(meta);     // selectKey 로 fileId 발급
                saved.add(meta);
            } catch (IOException e) {
                // 저장 실패 시 트랜잭션 롤백
                throw new UncheckedIOException("첨부파일 저장 실패", e);
            }
        }
        egovLogger.debug("saved {} files for board {}", saved.size(), boardId);
    }
}
