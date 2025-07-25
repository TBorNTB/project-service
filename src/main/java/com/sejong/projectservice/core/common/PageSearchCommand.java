package com.sejong.projectservice.core.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectSearchCommand {
    private int page;
    private int size;
    private String keyword;

    public static ProjectSearchCommand of(int page, int size, String keyword) {}
}
