package com.sejong.projectservice.application.file;

public record FileUploadRequest(
    String fileName,
    String contentType,
    String fileType
) {
}
