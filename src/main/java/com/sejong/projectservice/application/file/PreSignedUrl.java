package com.sejong.projectservice.application.file;

public record PreSignedUrl(
    String uploadUrl,
    String key,
    String downloadUrl,
    long expirationTime
) {
}
