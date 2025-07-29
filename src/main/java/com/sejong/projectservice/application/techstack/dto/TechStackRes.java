package com.sejong.projectservice.application.techstack.dto;

import com.sejong.projectservice.core.techstack.TechStack;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TechStackRes {
    private Long id;
    private String name;

    public static TechStackRes from(TechStack techStack) {
        return TechStackRes.builder()
                .id(techStack.getId())
                .name(techStack.getName())
                .build();
    }
}
