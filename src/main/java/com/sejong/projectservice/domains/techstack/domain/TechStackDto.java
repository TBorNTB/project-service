package com.sejong.projectservice.domains.techstack.domain;

import java.util.List;
import java.util.Objects;
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

    public static List<TechStackDto> from2(List<TechStackEntity> techStackEntities) {
        return techStackEntities.stream()
                .map(it->{
                    return TechStackDto.builder()
                            .id(it.getId())
                            .name(it.getName())
                            .build();
                }).toList();
    }

    public void update(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TechStackDto that = (TechStackDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
