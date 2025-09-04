package com.sejong.projectservice.application.techstack.service;

import com.sejong.projectservice.application.common.error.code.ErrorCode;
import com.sejong.projectservice.application.common.error.exception.ApiException;
import com.sejong.projectservice.application.techstack.dto.TechStackCreateReq;
import com.sejong.projectservice.application.techstack.dto.TechStackRes;
import com.sejong.projectservice.core.techstack.TechStack;
import com.sejong.projectservice.core.techstack.TechstackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TechStackService {

    private final TechstackRepository techstackRepository;

    @Transactional
    public TechStackRes createTechStack(TechStackCreateReq techstackCreateReq, String userRole) {
        validateAdminRole(userRole);
        TechStack techStack = techstackCreateReq.toDomain();
        TechStack savedTechstack = techstackRepository.save(techStack);
        return TechStackRes.from(savedTechstack);
    }

    private void validateAdminRole(String userRole) {
        if (!userRole.equals("ADMIN")) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "어드민 전용 권한입니다.");
        }
    }

    @Transactional(readOnly = true)
    public TechStackRes getTechStack(Long techStackId) {
        TechStack techStack = techstackRepository.findById(techStackId);
        return TechStackRes.from(techStack);
    }

    @Transactional
    public TechStackRes updateTechStack(Long techStackId, TechStackCreateReq request, String userRole) {
        validateAdminRole(userRole);
        TechStack techStack = techstackRepository.findById(techStackId);
        techStack.update(request.getName());
        TechStack savedTechStack = techstackRepository.save(techStack);
        return TechStackRes.from(savedTechStack);
    }

    @Transactional
    public void deleteTechStack(Long techStackId, String userRole) {
        validateAdminRole(userRole);
        techstackRepository.deleteById(techStackId);
    }
}
