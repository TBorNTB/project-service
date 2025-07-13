package com.sejong.projectservice.application.yorkie.dto.response;

import com.sejong.projectservice.core.yorkie.Yorkie;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class YorkieRegisterResponse {
    private Long projectId;
    private Long yorkieId;
    private String message;

    public static YorkieRegisterResponse from(Yorkie yorkie) {
        return YorkieRegisterResponse.builder()
                .projectId(yorkie.getProjectId())
                .yorkieId(yorkie.getYorkieId())
                .message("정상적으로 저장되었습니다.").build();
    }
}
