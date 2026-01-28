package com.sejong.projectservice.domains.qna.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionUpdateRequest {

    @NotBlank
    private String title;

    private List<String> categories;

    @NotBlank
    private String description;

    @NotBlank
    private String content;
}
