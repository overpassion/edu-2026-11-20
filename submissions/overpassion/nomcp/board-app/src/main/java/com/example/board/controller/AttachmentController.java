package com.example.board.controller;

import com.example.board.domain.Attachment;
import com.example.board.service.AttachmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/attachments")
public class AttachmentController {
    private final AttachmentService service;

    public AttachmentController(AttachmentService service) {
        this.service = service;
    }

    @PostMapping("/upload")
    public ResponseEntity<Attachment> upload(@RequestParam("boardId") String boardId,
                                            @RequestParam("file") MultipartFile file) throws IOException {
        Attachment att = new Attachment();
        att.setBoardId(boardId);
        att.setFileName(file.getOriginalFilename());
        att.setData(file.getBytes());
        Attachment saved = service.save(att);
        return ResponseEntity.ok(saved);
    }
}