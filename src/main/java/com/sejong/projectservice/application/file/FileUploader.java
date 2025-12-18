package com.sejong.projectservice.application.file;


import com.sejong.projectservice.core.common.file.Filepath;

public interface FileUploader {
    PreSignedUrl generatePreSignedUrl(String fileName, String contentType, String dirName);
    void delete(Filepath filepath);
    Filepath getFileUrl(String key);
}
