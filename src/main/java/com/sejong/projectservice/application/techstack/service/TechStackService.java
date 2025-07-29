package com.sejong.projectservice.application.techstack.service;

import com.sejong.projectservice.application.techstack.dto.TechStackCreateReq;
import com.sejong.projectservice.application.techstack.dto.TechStackRes;
import com.sejong.projectservice.core.techstack.TechStack;
import com.sejong.projectservice.core.techstack.TechstackRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TechStackService {

    private final TechstackRepository techstackRepository;

    @Transactional
    public TechStackRes createTechStack(TechStackCreateReq techstackCreateReq) {
        TechStack techStack = techstackCreateReq.toDomain();
        TechStack savedTechstack = techstackRepository.save(techStack);
        return TechStackRes.from(savedTechstack);
    }

    public TechStackRes getTechStack(Long techStackId) {
        TechStack techStack = techstackRepository.findById(techStackId);
        return TechStackRes.from(techStack);
    }

    @Transactional
    public TechStackRes updateTechStack(Long techStackId, TechStackCreateReq request) {
        TechStack techStack = techstackRepository.findById(techStackId);
        techStack.update(request.getName());
        TechStack savedTechStack = techstackRepository.save(techStack);
        return TechStackRes.from(savedTechStack);
    }

    @Transactional
    public void deleteTechStack(Long techStackId) {
        techstackRepository.deleteById(techStackId);
    }
}
