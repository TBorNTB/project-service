package com.sejong.projectservice.domains.techstack.dto;

import com.sejong.projectservice.domains.techstack.domain.TechStackEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TechStackRes {
    private Long id;
    private String name;

    public static TechStackRes from(TechStackEntity techStack) {
        return TechStackRes.builder()
                .id(techStack.getId())
                .name(techStack.getName())
                .build();
    }
}
