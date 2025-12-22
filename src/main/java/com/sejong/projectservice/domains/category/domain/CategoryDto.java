package com.sejong.projectservice.domains.category.domain;

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
public class CategoryDto {

    private Long id;
    private String name;
    private String description;

    public static CategoryDto of(String name) {
        return CategoryDto.builder()
                .id(null)
                .name(name)
                .build();
    }

    public static List<CategoryDto> from2(List<CategoryEntity> categoryEntities) {
        return categoryEntities.stream()
                .map(it->{
                    return CategoryDto.builder()
                            .id(it.getId())
                            .name(it.getName())
                            .description(it.getDescription())
                            .build();
                }).toList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CategoryDto that = (CategoryDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateDescription(String description) {
        this.description = description;
    }
}
