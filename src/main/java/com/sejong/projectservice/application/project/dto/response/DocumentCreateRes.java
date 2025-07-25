package com.sejong.projectservice.application.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentCreateRes {
    private String title;
    private String message;

    public static DocumentCreateRes from(String title, String message) {
        return DocumentCreateRes.builder()
                .title(title)
                .message(message)
                .build();
    }
}
