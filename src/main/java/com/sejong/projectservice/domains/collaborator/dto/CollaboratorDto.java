package com.sejong.projectservice.domains.collaborator.dto;

import com.sejong.projectservice.domains.collaborator.domain.CollaboratorEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CollaboratorDto {
    private Long id;
    private String collaboratorName;

    public static CollaboratorDto from(String collaboratorName) {
        return CollaboratorDto.builder()
                .id(null)
                .collaboratorName(collaboratorName)
                .build();
    }

    public static List<CollaboratorDto> toDtoList(List<CollaboratorEntity> collaboratorEntityEntities) {
        return collaboratorEntityEntities.stream()
                .map(it->{
                    return CollaboratorDto.builder()
                            .id(it.getId())
                            .collaboratorName(it.getCollaboratorName())
                            .build();
                }).toList();
    }
}
