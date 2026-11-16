package com.example.board.service;

import com.example.board.domain.Board;
import com.example.board.repository.BoardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class BoardService {
    private final BoardRepository repository;

    public BoardService(BoardRepository repository) {
        this.repository = repository;
    }

    public List<Board> findAll() {
        return repository.findAll();
    }

    public Optional<Board> findById(String id) {
        return repository.findById(id);
    }

    public Board create(Board board) {
        board.setBoardId(UUID.randomUUID().toString());
        return repository.save(board);
    }

    public Board update(String id, Board board) {
        board.setBoardId(id);
        return repository.save(board);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }
}