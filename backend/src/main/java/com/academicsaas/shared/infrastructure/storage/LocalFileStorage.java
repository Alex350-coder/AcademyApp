package com.academicsaas.shared.infrastructure.storage;

import com.academicsaas.shared.application.port.FileStorage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.storage.provider", havingValue = "local", matchIfMissing = true)
public class LocalFileStorage implements FileStorage {

    private static final Logger log = LoggerFactory.getLogger(LocalFileStorage.class);
    private final Path storagePath;

    public LocalFileStorage(@Value("${app.storage.local.path:./uploads}") String path) throws IOException {
        this.storagePath = Path.of(path).toAbsolutePath().normalize();
        Files.createDirectories(this.storagePath);
        log.info("Local file storage initialized at: {}", this.storagePath);
    }

    @Override
    public String upload(FileUpload command) {
        try {
            var extension = "";
            var lastDot = command.fileName().lastIndexOf('.');
            if (lastDot > 0) {
                extension = command.fileName().substring(lastDot);
            }

            var fileId = UUID.randomUUID().toString() + extension;
            var targetPath = storagePath.resolve(fileId);

            Files.write(targetPath, command.content());
            log.info("File uploaded: {} -> {}", command.fileName(), targetPath);

            return fileId;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file: " + command.fileName(), e);
        }
    }

    @Override
    public Optional<String> getUrl(String fileId) {
        var filePath = resolveWithinStorage(fileId);
        if (filePath.isPresent() && Files.exists(filePath.get())) {
            return Optional.of("/api/v1/files/" + fileId);
        }
        return Optional.empty();
    }

    @Override
    public void delete(String fileId) {
        var filePath = resolveWithinStorage(fileId);
        if (filePath.isEmpty()) {
            log.warn("Refused to delete file with path-traversing id: {}", fileId);
            return;
        }
        try {
            Files.deleteIfExists(filePath.get());
            log.info("File deleted: {}", fileId);
        } catch (IOException e) {
            log.error("Failed to delete file: {}", fileId, e);
        }
    }

    // Resolves fileId against the storage root and rejects the result if it
    // escapes that root (path traversal via "../" or an absolute fileId).
    private Optional<Path> resolveWithinStorage(String fileId) {
        var resolved = storagePath.resolve(fileId).normalize();
        if (!resolved.startsWith(storagePath)) {
            return Optional.empty();
        }
        return Optional.of(resolved);
    }
}
