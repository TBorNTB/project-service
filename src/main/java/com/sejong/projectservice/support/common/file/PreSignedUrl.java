package com.sejong.projectservice.support.common.file;

public record PreSignedUrl(
    String uploadUrl,
    String key,
    String downloadUrl,
    long expirationTime
) {
}
