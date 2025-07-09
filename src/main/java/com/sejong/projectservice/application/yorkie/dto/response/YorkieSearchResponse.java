package com.sejong.projectservice.application.yorkie.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class YorkieSearchResponse {
    private Long yorkieId;

    public static YorkieSearchResponse of(Long yorkieId) {
        return YorkieSearchResponse.builder()
                .yorkieId(yorkieId)
                .build();
    }
}
