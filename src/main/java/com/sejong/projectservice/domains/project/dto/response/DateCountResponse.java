package com.sejong.projectservice.domains.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DateCountResponse {
    private Long csCount;
    private Long newsCount;
    private Long projectCount;
    
    public static DateCountResponse of(Long csCount, Long newsCount, Long projectCount) {
        return DateCountResponse.builder()
                .csCount(csCount)
                .newsCount(newsCount)
                .projectCount(projectCount)
                .build();
    }
}

