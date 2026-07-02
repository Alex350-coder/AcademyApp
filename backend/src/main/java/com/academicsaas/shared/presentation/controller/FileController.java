package com.academicsaas.shared.presentation.controller;

import com.academicsaas.shared.application.port.FileStorage;
import com.academicsaas.shared.exception.NotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    private final FileStorage fileStorage;

    public FileController(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> upload(@RequestParam("file") MultipartFile file) {
        try {
            var fileId = fileStorage.upload(new FileStorage.FileUpload(
                file.getBytes(),
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize()
            ));
            return ResponseEntity.ok(new FileUploadResponse(fileId));
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<Resource> serveFile(@PathVariable String fileId) {
        var urlOpt = fileStorage.getUrl(fileId);
        if (urlOpt.isEmpty()) {
            throw new NotFoundException("File", fileId);
        }
        try {
            var path = Path.of(urlOpt.get().replace("/api/v1/files/", ""));
            var resolved = Path.of("./uploads").resolve(path).normalize();
            var resource = (Resource) new UrlResource(resolved.toUri());
            if (resource.exists() && resource.isReadable()) {
                var contentType = Files.probeContentType(resolved);
                return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                    .body(resource);
            }
        } catch (Exception e) {
            throw new NotFoundException("File", fileId);
        }
        throw new NotFoundException("File", fileId);
    }

    record FileUploadResponse(String fileId) {}
}
