package com.sejong.archiveservice.application.file;

public record FileUploadRequest(
    String fileName,
    String contentType,
    String fileType
) {
}
