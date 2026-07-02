package com.academicsaas.shared.application.port;

import java.util.Optional;

public interface FileStorage {
    String upload(FileUpload command);
    Optional<String> getUrl(String fileId);
    void delete(String fileId);

    record FileUpload(
        byte[] content,
        String fileName,
        String contentType,
        long contentLength
    ) {}
}
