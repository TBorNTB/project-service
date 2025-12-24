package com.sejong.projectservice.support.common.file;

public record FileUploadRequest(
    String fileName,
    String contentType,
    String fileType
) {
}
