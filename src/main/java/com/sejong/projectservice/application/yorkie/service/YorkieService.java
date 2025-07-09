package com.sejong.projectservice.application.yorkie.service;

import com.sejong.projectservice.application.yorkie.dto.response.YorkieRegisterResponse;
import com.sejong.projectservice.application.yorkie.dto.response.YorkieSearchResponse;
import com.sejong.projectservice.core.yorkie.Yorkie;
import com.sejong.projectservice.core.yorkie.YorkieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class YorkieService {

    private final YorkieRepository yorkieRepository;

    public YorkieRegisterResponse register(Long yorkieId, Long projectId) {
        Yorkie yorkie = Yorkie.of(yorkieId, projectId);
        Yorkie savedYorkie = yorkieRepository.save(yorkie);
        return YorkieRegisterResponse.from(savedYorkie);
    }

    public YorkieSearchResponse findYorkieId(Long projectId) {
        Long yorkieId = yorkieRepository.findByProjectId(projectId);
        return YorkieSearchResponse.of(yorkieId);
    }
}
