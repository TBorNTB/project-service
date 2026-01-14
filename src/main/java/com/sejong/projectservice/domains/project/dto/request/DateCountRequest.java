package com.sejong.projectservice.domains.project.dto.request;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DateCountRequest {
    private LocalDate startDate;
    private LocalDate endDate;
}

