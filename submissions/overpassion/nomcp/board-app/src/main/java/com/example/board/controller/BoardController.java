package com.example.board.controller;

import com.example.board.domain.Board;
import com.example.board.service.BoardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
public class BoardController {
    private final BoardService service;

    public BoardController(BoardService service) {
        this.service = service;
    }

    @GetMapping
    public List<Board> all() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Board> get(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Board create(@RequestBody Board board) {
        return service.create(board);
    }

    @PutMapping("/{id}")
    public Board update(@PathVariable String id, @RequestBody Board board) {
        return service.update(id, board);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}