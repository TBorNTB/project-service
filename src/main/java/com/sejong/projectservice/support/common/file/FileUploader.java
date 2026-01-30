package com.sejong.projectservice.support.common.file;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploader {
    // Presigned URL 방식
    PreSignedUrl generatePreSignedUrl(String fileName, String contentType, String dirName);
    String moveFile(String sourceKey, String targetDirectory);

    // 직접 업로드 방식
    String uploadFile(MultipartFile file, String directory, String fileName);

    // 공통
    void delete(String keyOrUrl);
    String getFileUrl(String keyOrUrl);
    String extractKeyFromUrl(String url);
}
