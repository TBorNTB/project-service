package com.sejong.projectservice.application.yorkie.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class YorkieRegisterRequest {
    private Long yorkieId;
    private Long projectId;
}
