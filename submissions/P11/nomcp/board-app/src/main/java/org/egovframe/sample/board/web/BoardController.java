package org.egovframe.sample.board.web;

import org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;
import org.egovframe.sample.board.model.Board;
import org.egovframe.sample.board.model.BoardFile;
import org.egovframe.sample.board.model.BoardSearch;
import org.egovframe.sample.board.service.BoardService;
import org.egovframe.sample.board.service.FileStorageService;
import jakarta.validation.Valid;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

/**
 * 게시판 웹 컨트롤러.
 * <p>eGovFrame {@link PaginationInfo} 로 페이지 내비게이션을 계산한다.</p>
 */
@Controller
@RequestMapping("/board")
public class BoardController {

    private static final int RECORD_COUNT_PER_PAGE = 10;
    private static final int PAGE_SIZE = 5;

    private final BoardService boardService;
    private final FileStorageService fileStorage;

    public BoardController(BoardService boardService, FileStorageService fileStorage) {
        this.boardService = boardService;
        this.fileStorage = fileStorage;
    }

    /** 목록 */
    @GetMapping("/list")
    public String list(@ModelAttribute("search") BoardSearch search,
                       @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                       Model model) {
        search.setCurrentPageNo(page);
        int total = boardService.countBoardList(search);

        PaginationInfo paginationInfo = new PaginationInfo();
        paginationInfo.setCurrentPageNo(search.getCurrentPageNo());
        paginationInfo.setRecordCountPerPage(RECORD_COUNT_PER_PAGE);
        paginationInfo.setPageSize(PAGE_SIZE);
        paginationInfo.setTotalRecordCount(total);

        search.setFirstRecordIndex(paginationInfo.getFirstRecordIndex());
        search.setRecordCountPerPage(RECORD_COUNT_PER_PAGE);

        List<Board> list = boardService.getBoardList(search);

        model.addAttribute("boardList", list);
        model.addAttribute("paginationInfo", paginationInfo);
        return "board/list";
    }

    /** 상세 */
    @GetMapping("/detail/{boardId}")
    public String detail(@PathVariable String boardId, Model model) {
        Board board = boardService.getBoard(boardId);
        if (board == null) {
            return "redirect:/board/list";
        }
        model.addAttribute("board", board);
        return "board/detail";
    }

    /** 등록 폼 */
    @GetMapping("/createForm")
    public String createForm(Model model) {
        if (!model.containsAttribute("board")) {
            model.addAttribute("board", new Board());
        }
        return "board/form";
    }
    /** 등록 처리 */
    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("board") Board board,
                         BindingResult bindingResult,
                         @RequestParam(value = "files", required = false) MultipartFile[] files,
                         RedirectAttributes rttr) {
        if (bindingResult.hasErrors()) {
            rttr.addFlashAttribute("org.springframework.validation.BindingResult.board", bindingResult);
            rttr.addFlashAttribute("board", board);
            return "redirect:/board/createForm";
        }
        Board saved = boardService.addBoard(board, files);
        return "redirect:/board/detail/" + saved.getBoardId();
    }

    /** 수정 폼 */
    @GetMapping("/editForm/{boardId}")
    public String editForm(@PathVariable String boardId, Model model) {
        if (!model.containsAttribute("board")) {
            Board board = boardService.getBoard(boardId);
            if (board == null) {
                return "redirect:/board/list";
            }
            model.addAttribute("board", board);
        }
        return "board/form";
    }

    /** 수정 처리 */
    @PostMapping("/update")
    public String update(@Valid @ModelAttribute("board") Board board,
                         BindingResult bindingResult,
                         @RequestParam(value = "files", required = false) MultipartFile[] files,
                         @RequestParam(value = "deletedFileIds", required = false) List<String> deletedFileIds,
                         RedirectAttributes rttr) {
        if (bindingResult.hasErrors()) {
            rttr.addFlashAttribute("org.springframework.validation.BindingResult.board", bindingResult);
            rttr.addFlashAttribute("board", board);
            return "redirect:/board/editForm/" + board.getBoardId();
        }
        boardService.modifyBoard(board, files, deletedFileIds);
        return "redirect:/board/detail/" + board.getBoardId();
    }

    /** 삭제 처리 */
    @PostMapping("/delete/{boardId}")
    public String delete(@PathVariable String boardId) {
        boardService.removeBoard(boardId);
        return "redirect:/board/list";
    }

    /** 첨부파일 다운로드 */
    @GetMapping("/file/{fileId}")
    public ResponseEntity<Resource> download(@PathVariable String fileId) {
        BoardFile meta = boardService.getBoardFile(fileId);
        if (meta == null) {
            return ResponseEntity.notFound().build();
        }
        Path path = fileStorage.resolve(meta);
        FileSystemResource resource = new FileSystemResource(path);
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        String encoded = URLEncoder.encode(meta.getFileNm(), StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename*=UTF-8''" + encoded)
                .body(resource);
    }
}
