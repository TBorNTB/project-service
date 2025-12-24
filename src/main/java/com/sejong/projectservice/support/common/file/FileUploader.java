package com.sejong.projectservice.support.common.file;


public interface FileUploader {
    PreSignedUrl generatePreSignedUrl(String fileName, String contentType, String dirName);
    void delete(Filepath filepath);
    Filepath getFileUrl(String key);
}
