package org.egovframe.sample.board.service;

import org.egovframe.sample.board.model.BoardFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * 첨부파일 물리 저장소 유틸.
 * <p>{@code egov.upload.dir}(실행 위치 기준 상대/절대 경로) 아래에 파일을 저장한다.</p>
 */
@Component
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);

    private final Path rootDir;

    public FileStorageService(@Value("${egov.upload.dir:upload-files}") String uploadDir) {
        this.rootDir = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    public Path getRootDir() {
        return rootDir;
    }

    /**
     * MultipartFile 을 저장하고 DB 에 기록할 {@link BoardFile} 메타데이터를 만든다.
     */
    public BoardFile store(String boardId, MultipartFile multipart) throws IOException {
        if (!Files.exists(rootDir)) {
            Files.createDirectories(rootDir);
        }

        String original = sanitize(multipart.getOriginalFilename());
        String storedName = UUID.randomUUID().toString().replace("-", "") + "_" + original;

        Path target = rootDir.resolve(storedName).normalize();
        // 경로 이탈(traversal) 방지
        if (!target.startsWith(rootDir)) {
            throw new IOException("허용되지 않는 파일 경로입니다: " + storedName);
        }

        try (InputStream in = multipart.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }

        BoardFile file = new BoardFile();
        file.setBoardId(boardId);
        file.setFileNm(original);
        file.setFilePath(storedName);
        file.setFileSize(multipart.getSize());

        log.debug("stored file: boardId={} storedName={} size={}", boardId, storedName, multipart.getSize());
        return file;
    }

    /**
     * 저장된 첨부파리를 읽기 위한 물리 경로.
     */
    public Path resolve(BoardFile meta) {
        return rootDir.resolve(meta.getFilePath()).normalize();
    }

    /**
     * 물리 파일 삭제. 실패해도 예외를 던지지 않는다(DB 정합성 우선).
     */
    public void delete(BoardFile meta) {
        if (meta == null) {
            return;
        }
        try {
            Files.deleteIfExists(resolve(meta));
        } catch (IOException e) {
            log.warn("첨부파일 삭제 실패: {}", meta.getFilePath(), e);
        }
    }

    /** 경로 구분자 등 위험 문자 제거 */
    private String sanitize(String name) {
        if (name == null || name.isBlank()) {
            return "unnamed";
        }
        // 윈도우 경로까지 고려해 마지막 구분자 이후 이름만 사용
        String base = name.replaceAll("\\\\", "/");
        base = base.substring(base.lastIndexOf('/') + 1);
        return base.replaceAll("[^A-Za-z0-9._-\\u3131-\\uD7A3 ]", "_");
    }
}
