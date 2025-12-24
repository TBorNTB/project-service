package com.sejong.projectservice.domains.yorkie.controller;

import com.sejong.projectservice.domains.yorkie.dto.request.CheckYorkieRequest;
import com.sejong.projectservice.domains.yorkie.dto.response.CheckYorkieResponse;
import com.sejong.projectservice.domains.yorkie.service.YorkieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/yorkie")
public class YorkieController {
    private final YorkieService yorkieService;

    @PostMapping("/check")
    public ResponseEntity<CheckYorkieResponse> checkYorkie(
            @RequestBody CheckYorkieRequest checkYorkieRequest
    ) {
        return yorkieService.checkYorkie(checkYorkieRequest);
    }
}
