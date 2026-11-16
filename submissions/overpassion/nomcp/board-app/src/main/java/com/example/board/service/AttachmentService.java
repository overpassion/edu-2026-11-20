package com.example.board.service;

import com.example.board.domain.Attachment;
import com.example.board.repository.AttachmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AttachmentService {
    private final AttachmentRepository repository;

    public AttachmentService(AttachmentRepository repository) {
        this.repository = repository;
    }

    public Attachment save(Attachment attachment) {
        return repository.save(attachment);
    }

    public List<Attachment> findAll() {
        return repository.findAll();
    }
}