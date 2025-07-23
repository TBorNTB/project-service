package com.sejong.projectservice.application.yorkie.controller;

import com.sejong.projectservice.application.yorkie.dto.request.CheckYorkieRequest;
import com.sejong.projectservice.application.yorkie.dto.response.CheckYorkieResponse;
import com.sejong.projectservice.application.yorkie.service.YorkieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/yorkie")
public class YorkieController {
    private final YorkieService yorkieService;

    @PostMapping("/check")
    public ResponseEntity<CheckYorkieResponse> checkYorkie(
            @RequestBody CheckYorkieRequest checkYorkieRequest
    ){

        CheckYorkieResponse response = yorkieService.checkYorkie(checkYorkieRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
