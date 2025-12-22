package com.sejong.projectservice.domains.techstack.dto;

import com.sejong.projectservice.domains.techstack.domain.TechStackDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TechStackCreateReq {
    private String name;

    public TechStackDto toDomain() {
        return TechStackDto.builder()
                .id(null)
                .name(this.name)
                .build();
    }
}
