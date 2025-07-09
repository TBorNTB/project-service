package com.sejong.projectservice.infrastructure.yorkie;

import com.sejong.projectservice.core.yorkie.Yorkie;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "yorkie",
        indexes = {
                @Index(name = "uk_project_id", columnList = "projectId", unique = true)
        })

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class YorkieEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long projectId;
    private Long yorkieId; //일단은 yorkieId가 바뀌지 않는다는 가정

    public static YorkieEntity from(Yorkie yorkie) {
        return YorkieEntity.builder()
                .id(null)
                .projectId(yorkie.getProjectId())
                .yorkieId(yorkie.getYorkieId())
                .build();
    }

    public Yorkie toDomain() {
        return Yorkie.builder()
                .projectId(this.projectId)
                .yorkieId(this.yorkieId)
                .build();
    }
}
