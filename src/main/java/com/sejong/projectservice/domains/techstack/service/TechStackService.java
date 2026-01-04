package com.sejong.projectservice.domains.techstack.service;

import com.sejong.projectservice.domains.techstack.domain.TechStackEntity;
import com.sejong.projectservice.domains.techstack.repository.TechStackRepository;
import com.sejong.projectservice.domains.techstack.dto.TechStackCreateReq;
import com.sejong.projectservice.domains.techstack.dto.TechStackRes;
import com.sejong.projectservice.support.common.exception.BaseException;
import com.sejong.projectservice.support.common.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TechStackService {

    private final TechStackRepository techStackRepository;

    @Transactional
    public TechStackRes createTechStack(TechStackCreateReq techstackCreateReq, String userRole) {
        validateAdminRole(userRole);
        TechStackEntity techStack = TechStackEntity.from(techstackCreateReq);
        TechStackEntity savedTechStack = techStackRepository.save(techStack);
        return TechStackRes.from(savedTechStack);
    }

    private void validateAdminRole(String userRole) {
        if (!userRole.equals("ADMIN")) {
            throw new BaseException(ExceptionType.REQUIRED_ADMIN);
        }
    }

    @Transactional(readOnly = true)
    public TechStackRes getTechStack(Long techStackId) {
        TechStackEntity techStackEntity = techStackRepository.findById(techStackId)
                .orElseThrow(() -> new BaseException(ExceptionType.TECHSTACK_NOT_FOUND));
        return TechStackRes.from(techStackEntity);
    }

    @Transactional
    public TechStackRes updateTechStack(Long techStackId, TechStackCreateReq request, String userRole) {
        validateAdminRole(userRole);
        TechStackEntity techStackEntity = techStackRepository.findById(techStackId)
                .orElseThrow(() -> new BaseException(ExceptionType.TECHSTACK_NOT_FOUND));
        techStackEntity.update(request.getName());
        return TechStackRes.from(techStackEntity);
    }

    @Transactional
    public void deleteTechStack(Long techStackId, String userRole) {
        validateAdminRole(userRole);
        TechStackEntity techStackEntity = techStackRepository.findById(techStackId)
                .orElseThrow(() -> new BaseException(ExceptionType.TECHSTACK_NOT_FOUND));
        techStackRepository.deleteById(techStackEntity.getId());
    }
}
