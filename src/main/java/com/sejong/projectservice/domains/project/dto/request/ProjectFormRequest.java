package com.sejong.projectservice.domains.project.dto.request;

import com.sejong.projectservice.support.common.constants.ProjectStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectFormRequest {

    private String title;
    private String description;
    private String thumbnail;
    private String content;

    private ProjectStatus projectStatus;

    private List<String> categories;
    private List<String> collaborators;
    private List<String> techStacks;
    private List<String> subGoals;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    @Schema(description = "썸네일 이미지 key (presigned URL 업로드 후 받은 key)")
    private String thumbnailKey;

    @Schema(description = "에디터 본문에 삽입된 이미지 key 목록")
    private List<String> contentImageKeys;
}
