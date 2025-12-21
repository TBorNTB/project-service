package com.sejong.projectservice.domains.techstack.dto;

import com.sejong.projectservice.domains.techstack.domain.TechStack;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TechStackCreateReq {
    private String name;

    public TechStack toDomain() {
        return TechStack.builder()
                .id(null)
                .name(this.name)
                .build();
    }
}
