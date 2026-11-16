package org.egovframe.sample.board.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 루트 요청을 게시판 목록으로 연결.
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/board/list";
    }
}
