package com.sejong.projectservice.application.yorkie.dto.request;

import com.sejong.projectservice.application.yorkie.service.YorkieService.DocumentAttribute;
import com.sejong.projectservice.application.yorkie.service.YorkieService.YorkieMethod;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckYorkieRequest {
    private String token;
    private YorkieMethod method;
    private List<DocumentAttribute> attributes;
}

