package com.sejong.projectservice.domains.techstack.dto;

import java.util.List;

import com.sejong.projectservice.domains.techstack.domain.TechStackEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TechStackDto {

    private Long id;
    private String name;

    public static TechStackDto of(String name) {
        return TechStackDto.builder()
                .id(null)
                .name(name)
                .build();
    }

    public static List<TechStackDto> fromList(List<TechStackEntity> techStackEntities) {
        return techStackEntities.stream()
                .map(it->{
                    return TechStackDto.builder()
                            .id(it.getId())
                            .name(it.getName())
                            .build();
                }).toList();
    }
}
