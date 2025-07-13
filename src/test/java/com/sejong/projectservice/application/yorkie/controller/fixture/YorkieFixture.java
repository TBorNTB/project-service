package com.sejong.projectservice.application.yorkie.controller.fixture;

import com.sejong.projectservice.core.yorkie.Yorkie;

public class YorkieFixture {
    public static Yorkie createYorkie() {
        return Yorkie.builder()
                .yorkieId(1L)
                .projectId(1L)
                .build();
    }
}
