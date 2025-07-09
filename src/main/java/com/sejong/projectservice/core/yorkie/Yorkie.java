package com.sejong.projectservice.core.yorkie;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Yorkie {
    private Long yorkieId;
    private Long projectId;

    public static Yorkie of(Long yorkieId, Long projectId) {
        return Yorkie.builder()
                .yorkieId(yorkieId)
                .projectId(projectId)
                .build();
    }
}
