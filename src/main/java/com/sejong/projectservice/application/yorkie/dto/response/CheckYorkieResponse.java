package com.sejong.projectservice.application.yorkie.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckYorkieResponse {
    private Boolean allowed;
    private String reason = "";
}
