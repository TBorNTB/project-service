package com.sejong.projectservice.application.yorkie.controller;

import com.sejong.projectservice.application.yorkie.dto.request.YorkieRegisterRequest;
import com.sejong.projectservice.application.yorkie.dto.response.YorkieRegisterResponse;
import com.sejong.projectservice.application.yorkie.dto.response.YorkieSearchResponse;
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

    @PostMapping("/register")
    public ResponseEntity<YorkieRegisterResponse> register(
            @RequestBody YorkieRegisterRequest yorkieRegisterRequest
    ){

        YorkieRegisterResponse response = yorkieService.register(yorkieRegisterRequest.getYorkieId(), yorkieRegisterRequest.getProjectId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<YorkieSearchResponse> searchYorkieId(
            @RequestParam(name="projectId") Long projectId
    ){
        YorkieSearchResponse response = yorkieService.findYorkieId(projectId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

}
