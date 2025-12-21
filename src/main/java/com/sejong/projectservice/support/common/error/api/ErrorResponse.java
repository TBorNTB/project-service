package com.sejong.projectservice.support.common.error.api;

import com.sejong.projectservice.support.common.error.code.ErrorCodeIfs;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {
    private Integer resultCode;
    private String resultMessage;
    private String resultDescription;

    public static ErrorResponse ERROR(ErrorCodeIfs errorCodeIfs, String description) {
        return ErrorResponse.builder()
                .resultCode(errorCodeIfs.getErrorCode())
                .resultMessage(errorCodeIfs.getDescription())
                .resultDescription(description)
                .build();
    }
}
